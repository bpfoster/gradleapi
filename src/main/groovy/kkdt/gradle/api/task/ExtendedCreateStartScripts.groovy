/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task

import org.gradle.api.internal.plugins.StartScriptGenerator
import org.gradle.api.tasks.application.CreateStartScripts

class ExtendedCreateStartScripts extends CreateStartScripts {
   /**
    * An extended details is a map of already transformed key-value pairs.
    */
   def extendedDetails = [:];
   
   public void generate() {
      super.generate();
  }
}
