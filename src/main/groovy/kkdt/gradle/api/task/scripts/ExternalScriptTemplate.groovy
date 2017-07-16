/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task.scripts

import java.nio.file.Files
import java.nio.file.Paths

import org.gradle.api.file.FileCollection
import org.gradle.api.resources.TextResource
import org.gradle.api.tasks.TaskDependency

/**
 * <p>
 * A Text Resource that wraps a script template file.
 * </p>
 *
 */
class ExternalScriptTemplate implements TextResource {
   static final String defaultEncoding = "UTF-8";
   
   File file;
   
   @Override
   public File asFile() {
      return file;
   }

   @Override
   public File asFile(String arg0) {
      return file;
   }

   @Override
   public Reader asReader() {
      return openStream();
   }

   @Override
   public String asString() {
      try {
         byte[] encoded = Files.readAllBytes(Paths.get(file.toURI()));
         return new String(encoded, defaultEncoding);
      } catch (Exception e) {
         throw new IllegalStateException("Cannot read file " + file + ": " + e.message, e);
      }
   }

   @Override
   public TaskDependency getBuildDependencies() {
      throw new UnsupportedOperationException("Cannot create TaskDependency for file " + file);
   }

   @Override
   public FileCollection getInputFiles() {
      throw new UnsupportedOperationException("Cannot create FileCollection for file " + file);
   }

   @Override
   public Object getInputProperties() {
      throw new UnsupportedOperationException("Cannot create input properties for file " + file);
   }
   
   public Reader openStream() throws IOException {
      if(file == null || !file.exists()) {
         throw new IllegalStateException("Could not find file resource " + file);
      }
      InputStream stream = new FileInputStream(file);
      return new BufferedReader(new InputStreamReader(stream, defaultEncoding));
   }

}
