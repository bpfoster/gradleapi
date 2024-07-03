/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task.scripts

import org.gradle.api.internal.plugins.DefaultJavaAppStartScriptGenerationDetails
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails

/**
 * <p>
 * Script template model that wraps <code>JavaAppStartScriptGenerationDetails</code>
 * specifications as well as a generic map for key-value bindings.
 * </p>
 * 
 * @author thinh ho
 *
 */
class ExtendedJavaStartScriptGenerationDetails implements JavaAppStartScriptGenerationDetails {
   /**
    * The Gradle default details definition.
    */
   DefaultJavaAppStartScriptGenerationDetails details;
   /**
    * An extended details is a map of already transformed key-value pairs.
    */
   Map<String, String> extendedDetails = [:];
   
   @Override
   public String getAppNameSystemProperty() {
      return details.getAppNameSystemProperty();
   }
   
   @Override
   public String getApplicationName() {
      return details.getApplicationName();
   }
   
   @Override
   public List<String> getClasspath() {
      return details.getClasspath();
   }

   @Override
   List<String> getModulePath() {
      return details.getModulePath();
   }

   @Override
   public List<String> getDefaultJvmOpts() {
      return details.getDefaultJvmOpts();
   }
   
   @Override
   public String getExitEnvironmentVar() {
      return details.getExitEnvironmentVar();
   }
   
   @Override
   public String getMainClassName() {
      return details.getMainClassName();
   }
   
   @Override
   public String getOptsEnvironmentVar() {
      return details.getOptsEnvironmentVar();
   }
   
   @Override
   public String getScriptRelPath() {
      return details.getScriptRelPath();
   }
}
