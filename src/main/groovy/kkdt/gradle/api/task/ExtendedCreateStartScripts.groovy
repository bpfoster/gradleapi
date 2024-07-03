/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task

import kkdt.gradle.api.task.scripts.ExtendedJavaStartScriptGenerationDetails
import kkdt.gradle.api.task.scripts.ExternalScriptGenerator
import kkdt.gradle.api.task.scripts.ExternalScriptTemplate
import kkdt.gradle.api.task.scripts.ExternalTemplateStartScriptGenerator
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.plugins.DefaultJavaAppStartScriptGenerationDetails
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.application.CreateStartScripts
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails

import java.util.stream.Collectors

/**
 * <p>
 * An extension of the Gradle <code>CreateStartScripts</code> task that allows
 * the following additional task parameters:
 * <ol>
 * <li>unixScriptTemplate : The file path to the unix script template (i.e. /opt/templates/unixScript.txt)</li>
 * <li>extendedDetails : A map of bindings from the template</li>
 * <li>scriptRelativePath : The relative path where generated scripts will be placed (default 'bin', i.e. bin/scriptName)</li>
 * <li>libRelativePath : The relative path where dependencies will be placed (default 'lib', i.e. lib/jarfile)</li>
 * <li>includeWindowsScript : Determine whether or not to include the Windows script generation</li>
 * </ol>
 * @author thinh ho
 *
 */
class ExtendedCreateStartScripts extends CreateStartScripts {
   /**
    * An extended details is a map of already transformed key-value pairs.
    */
   @Internal
   Map<String, String> extendedDetails = [:];
   @Input
   def unixScriptTemplate;
   @Input
   def scriptRelativePath;
   @Input
   def includeWindowsScript = false;
   
   @Override
   public void generate() {
      if(unixScriptTemplate == null) {
         logger.info('unixScriptTemplate not provided, using default unixStartScriptGenerator: ' + unixStartScriptGenerator.class.name);
      } else {
         logger.info("unixScriptTemplate file used for script generation: ${unixScriptTemplate} + ${unixScriptTemplate.class.name}");
         // build the template
         def fileTemplate = new ExternalScriptTemplate();
         fileTemplate.file = unixScriptTemplate;
         // configure the custom unix script template generator
         ExternalTemplateStartScriptGenerator unixScriptGenerator = new ExternalTemplateStartScriptGenerator();
         unixScriptGenerator.template = fileTemplate;
         unixScriptGenerator.lineSeparator = "\n";
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
      logger.info("UNIX script generated " + unixScript);
      if(includeWindowsScript) {
         generator.generateWindowsScript(windowsScript);
         logger.info("Windows script generated " + windowsScript);
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
         mainClass.getOrNull(),
         toStringList(defaultJvmOpts),
         toStringList(getRelativeClasspath()),
         getRelativePath(javaModuleDetector.inferModulePath(mainModule.isPresent(), getClasspath())),
         scriptRelativePath == null ? 'lib' + File.separator + unixScript.name : scriptRelativePath,
         null);
      logger.info('Generator set with extended details: ' + extendedDetails)
      scriptDetails.extendedDetails = extendedDetails;
      return scriptDetails;
   }

   private static List<String> getRelativePath(FileCollection path) {
      return path.getFiles().stream().map(input -> "lib/" + input.getName()).collect(Collectors.toList());
   }

   private static List<String> toStringList(Iterable<Object> items) {
      List<String> retVal = new ArrayList<>()
      items.forEach { item -> retVal.add(item.toString()) }
      return retVal;
   }
   
   // Copied from parent source code
   /*private Iterable<String> getRelativeClasspath() {
      return Iterables.transform(getClasspath().getFiles(), new Function<File, String>() {
          @Override
          public String apply(File input) {
              return libRelativePath == null ? 'lib' + File.separator + input.name : libRelativePath + File.separator + input.name;
          }
      });
   }*/
}
