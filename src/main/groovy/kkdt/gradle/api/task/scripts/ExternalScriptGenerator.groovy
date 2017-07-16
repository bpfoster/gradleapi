/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task.scripts

import org.gradle.api.Action
import org.gradle.internal.IoActions
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails
import org.gradle.jvm.application.scripts.ScriptGenerator

/**
 * <p>
 * The master controller for start scripts generation, wrapping the details and
 * both the Unix and Windows script generators.
 * </p>
 * 
 * @author thinh ho
 *
 */
class ExternalScriptGenerator {
   JavaAppStartScriptGenerationDetails details;
   ScriptGenerator unixStartScriptGenerator;
   ScriptGenerator windowsStartScriptGenerator;
   
   /**
    * <p>
    * @param unixScript
    */
   public void generateUnixScript(File unixScript) {
      IoActions.writeTextFile(unixScript, new Action<BufferedWriter>() {
         @Override
         public void execute(BufferedWriter writer) {
             unixStartScriptGenerator.generateScript(details, writer);
         }
      });
   }

   public void generateWindowsScript(File windowsScript) {
      IoActions.writeTextFile(windowsScript, new Action<BufferedWriter>() {
         @Override
         public void execute(BufferedWriter writer) {
             unixStartScriptGenerator.generateScript(details, writer);
         }
      });
   }
}
