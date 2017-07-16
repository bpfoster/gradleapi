/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task

import org.gradle.api.internal.plugins.DefaultJavaAppStartScriptGenerationDetails
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.api.tasks.application.CreateStartScripts
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails
import org.gradle.util.CollectionUtils
import org.gradle.util.TextUtil

import com.google.common.base.Function
import com.google.common.collect.Iterables

import kkdt.gradle.api.task.scripts.ExtendedJavaStartScriptGenerationDetails
import kkdt.gradle.api.task.scripts.ExternalScriptGenerator
import kkdt.gradle.api.task.scripts.ExternalScriptTemplate
import kkdt.gradle.api.task.scripts.ExternalTemplateStartScriptGenerator

/**
 * <p>
 * An extension of the Gradle <code>CreateStartScripts</code> task that allows
 * the following additional task parameters:
 * <ol>
 * <li>unixScriptTemplate : The file path to the unix script template (i.e. /opt/templates/unixScript.txt)</li>
 * <li>extendedDetails : A map of bindings from the template</li>
 * <li>scriptRelativePath : The relative path where generated scripts will be placed (i.e. bin/scriptName)</li>
 * <li>includeWindowsScript : Determine whether or not to include the Windows script generation</li>
 * </ol>
 * @author thinh ho
 *
 */
class ExtendedCreateStartScripts extends CreateStartScripts {
   def unixScriptTemplate;
   /**
    * An extended details is a map of already transformed key-value pairs.
    */
   def extendedDetails = [:];
   def scriptRelativePath;
   def includeWindowsScript = false;
   
   @Override
   public void generate() {
      if(unixScriptTemplate == null) {
         logger.info('unixScriptTemplate not provided, using default unixStartScriptGenerator: ' + unixStartScriptGenerator.class.name);
      } else {
         logger.info('unixScriptTemplate file used for script generation: ' + unixScriptTemplate + ", " + unixScriptTemplate.class.name);
         // build the template
         def fileTemplate = new ExternalScriptTemplate();
         fileTemplate.file = unixScriptTemplate;
         // configure the custom unix script template generator
         ExternalTemplateStartScriptGenerator unixScriptGenerator = new ExternalTemplateStartScriptGenerator();
         unixScriptGenerator.template = fileTemplate;
         unixScriptGenerator.lineSeparator = TextUtil.getUnixLineSeparator();
         unixScriptGenerator.bindingFactory = StartScriptTemplateBindingFactory.unix();
         // set the task unix script template generator to the custom generator
         unixStartScriptGenerator = unixScriptGenerator;
         logger.info("unixStartScriptGenerator set to " + unixScriptGenerator.class.name);
      }
      
      // build the generator
      ExternalScriptGenerator generator = new ExternalScriptGenerator();
      generator.unixStartScriptGenerator = unixStartScriptGenerator;
      generator.windowsStartScriptGenerator = windowsStartScriptGenerator;
      generator.details = createStartScriptGenerationDetails();
      
      // generate the scripts
      generator.generateUnixScript(unixScript);
      logger.info("UNIX script generated");
      if(includeWindowsScript) {
         generator.generateWindowsScript(windowsScript);
         logger.info("Windows script generated");
      } else {
         logger.info("Windows script generation skipped");
      }
   }
   
   /**
    * Create the details that this task properties respectively build. 
    */
   protected JavaAppStartScriptGenerationDetails createStartScriptGenerationDetails() {
      ExtendedJavaStartScriptGenerationDetails scriptDetails = new ExtendedJavaStartScriptGenerationDetails();
      scriptDetails.details = new DefaultJavaAppStartScriptGenerationDetails(
         applicationName,
         optsEnvironmentVar,
         exitEnvironmentVar,
         mainClassName,
         CollectionUtils.toStringList(defaultJvmOpts),
         CollectionUtils.toStringList(getRelativeClasspath()),
         scriptRelativePath, // generator.setScriptRelPath("bin/" + getUnixScript().getName());
         null);
      logger.info('Generator set with extended details: ' + extendedDetails)
      scriptDetails.extendedDetails = extendedDetails;
      return scriptDetails;
   }
   
   // Copied from parent source code
   private Iterable<String> getRelativeClasspath() {
      return Iterables.transform(getClasspath().getFiles(), new Function<File, String>() {
          @Override
          public String apply(File input) {
              return "lib/" + input.getName();
          }
      });
   }
}
