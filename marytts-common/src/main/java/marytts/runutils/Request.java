/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.runutils;

import java.lang.reflect.Constructor;
import java.io.StringReader;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Arrays;
import marytts.io.MaryIOException;
import marytts.MaryException;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import marytts.config.MaryConfiguration;
import marytts.data.Utterance;
import marytts.io.serializer.Serializer;
import marytts.modules.MaryModule;
import marytts.modules.ModuleRegistry;
import marytts.util.MaryRuntimeUtils;
import marytts.util.MaryUtils;
import marytts.util.io.FileUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.LogManager;

import marytts.data.Utterance;

/**
 * A request consists of input data, a desired output data type and the means to
 * process the input data into the data of the output type.<br>
 * <br>
 * A request is used as follows. First, its basic properties are set in the
 * constructor, such as its input and output types. Second, the input data is
 * provided to the request either by directly setting it
 * (<code>setInputData()</code>) or by reading it from a Reader
 * (<code>readInputData()</code>). Third, the request is processed
 * (<code>process()</code>). Finally, the output data is either accessed
 * directly (<code>getOutputData()</code>) or written to an output stream
 * (<code>writeOutputData</code>).
 */
public class Request {
    protected String configuration;
    protected String input_data;

    protected String outputTypeParams;
    protected Locale defaultLocale;

    protected int id;
    protected Logger logger;
    protected Utterance inputData;
    protected Utterance outputData;
    protected Serializer output_serializer;
    protected boolean abortRequested = false;
    protected Appender appender;

    // Keep track of timing info for each module
    // (map MaryModule onto Long)
    protected Map<MaryModule, Long> timingInfo;

    public Request(Appender app, String configuration, String input_data) {
        this.logger = LogManager.getLogger("R " + id);
        this.appender = app;

        if (app != null) {
            ((org.apache.logging.log4j.core.Logger) this.logger).addAppender(app);
        }

        this.configuration = configuration;
        this.input_data = input_data;

        timingInfo = new HashMap<MaryModule, Long>();
    }

    public void process() throws Exception {

        assert Mary.getCurrentState() == Mary.STATE_RUNNING;

        // Parser configuration
        final Properties configuration_properties = new Properties();
        configuration_properties.load(new StringReader(this.configuration));

        // Input serializer reflection
        Class<?> clazz;
        Constructor<?> ctor;
        Serializer input_serializer = null;
        try {
            clazz = Class.forName(configuration_properties.get("input_serializer").toString());
            ctor = clazz.getConstructor();
            input_serializer = (Serializer) ctor.newInstance(new Object[] {});
        } catch (ClassNotFoundException ex) {
        }
        if (input_serializer == null) {
            throw new MaryException("input serializer class \"" +
                                    configuration_properties.get("input_serializer") + "\" doesn't exist");
        }

        // Input serializer reflection
        output_serializer = null;
        try {
            clazz = Class.forName(configuration_properties.get("output_serializer").toString());
            ctor = clazz.getConstructor();
            this.output_serializer = (Serializer) ctor.newInstance(new Object[] {});
        } catch (ClassNotFoundException ex) {
        }
        if (output_serializer == null) {
            throw new MaryException("output serializer class \"" +
                                    configuration_properties.get("output_serializer") + "\" doesn't exist");
        }

        // Module sequence reflexion (FIXME: check if module is existing !)
        List<MaryModule> usedModules = new ArrayList<MaryModule>();
        String module_names = (String) configuration_properties.get("modules");
        if (module_names != null) {
            List<String> module_name_list = Arrays.asList(StringUtils.split(module_names));

            for (String module_class_name : module_name_list) {
                logger.debug("trying to load the following class " + module_class_name);

                MaryModule cur_module = null;
                cur_module = ModuleRegistry.getModule(Class.forName(module_class_name));
                if (cur_module == null) {
                    throw new MaryException("Cannot load module \"" + module_class_name +
                                            "\" as it is not existing");
                }

                usedModules.add(cur_module);
            }
        }

        // Define the data
        Utterance input_mary_data = input_serializer.load(this.input_data);

        // Start to achieve the process
        long startTime = System.currentTimeMillis();

        logger.info("Handling request using the following modules:");
        for (MaryModule m : usedModules) {
            logger.info("- " + m.getClass().getName());
        }
        outputData = input_mary_data;
        for (MaryModule m : usedModules) {
            if (abortRequested) {
                break;
            }

            logger.info("Next module: " + m.getClass().getName());

            // Start module if needed
            logger.debug("Starting the module");
            if (m.getState() == MaryModule.MODULE_OFFLINE) {
                // This should happen only in command line mode:
		logger.info("Starting module " + m.getClass().getName());
                m.startup();
                assert m.getState() == MaryModule.MODULE_RUNNING;
            }
            long moduleStartTime = System.currentTimeMillis();

            // Assess the input is ok
            m.checkInput(outputData);

            // Process the module
            Utterance outData = null;
            try {
		// FIXME: what about the configuration and the logger
                outData = m.process(outputData, this.appender);
            } catch (Exception e) {
                throw new MaryException("Module " + m.getClass().getName() + ": Problem processing the data.", e);
            }

            if (outData == null) {
                throw new NullPointerException("Module " + m.getClass().getName() + " returned null. This should not happen.");
            }

            outputData = outData;

            long moduleStopTime = System.currentTimeMillis();
            long delta = moduleStopTime - moduleStartTime;
            Long soFar = timingInfo.get(m);
            if (soFar != null) {
                timingInfo.put(m, new Long(soFar.longValue() + delta));
            } else {
                timingInfo.put(m, new Long(delta));
            }

	    // // FIXME: fix memory part
            // if (MaryRuntimeUtils.veryLowMemoryCondition()) {
            //     logger.info("Very low memory condition detected (only " + MaryUtils.availableMemory()
            //                 + " bytes left). Triggering garbage collection.");
            //     Runtime.getRuntime().gc();
            //     logger.info("After garbage collection: " + MaryUtils.availableMemory() + " bytes available.");
            // }
        }

        long stopTime = System.currentTimeMillis();
        logger.info("Request processed in " + (stopTime - startTime) + " ms.");
        for (MaryModule m : usedModules) {
            logger.info("   " + m.getClass().getName() + " took " + timingInfo.get(m) + " ms");
        }
    }

    public int getId() {
        return id;
    }

    /**
     * Inform this request that any further processing does not make sense.
     */
    public void abort() {
        logger.info("Requesting abort.");
        abortRequested = true;
    }

    /**
     * Set the input data directly, in case it is already in the form of a
     * Utterance object.
     *
     * @param input_data
     *            inputData
     */
    public void setInputData(String input_data) {
        this.input_data = input_data;
    }

    /**
     * Read the input data from a Reader.
     *
     * @param inputReader
     *            inputReader
     * @throws Exception
     *             Exception
     */
    public void readInputData(Reader inputReader) throws Exception {
        String inputText = FileUtils.getReaderAsString(inputReader);
        setInputData(inputText);
    }

    /**
     * Direct access to the output data.
     *
     * @return outputdata
     */
    public Utterance getOutputData() {
        return outputData;
    }

    public Object serializeFinaleUtterance() throws MaryIOException {
        return output_serializer.export(this.outputData);
    }
}