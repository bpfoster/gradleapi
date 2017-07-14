/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task.scripts

import org.gradle.api.Transformer
import org.gradle.api.internal.plugins.DefaultTemplateBasedStartScriptGenerator
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.api.internal.resources.CharSourceBackedTextResource
import org.gradle.api.resources.TextResource
import org.gradle.internal.impldep.com.google.common.base.Charsets
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails
import org.gradle.util.TextUtil

import com.google.common.io.CharSource

class ExternalTemplateStartScriptGenerator extends DefaultTemplateBasedStartScriptGenerator {
   
   protected static TextResource utf8FilePathResource(final File file) {
      return new CharSourceBackedTextResource("Filepath resource '" + file.absolutePath + "'", new CharSource() {
         @Override
         public Reader openStream() throws IOException {
            if(!file.exists()) {
               throw new IllegalStateException("Could not find file path resource " + file.absolutePath);
            }
            InputStream stream = new FileInputStream(file);
            return new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
         }
      });
   }
   
   Transformer<Map<String, String>, JavaAppStartScriptGenerationDetails> bindingFactory;
   
   public ExternalTemplateStartScriptGenerator(File fileTemplate) {
      super(TextUtil.getUnixLineSeparator(), StartScriptTemplateBindingFactory.unix(), utf8FilePathResource(fileTemplate));
      bindingFactory = StartScriptTemplateBindingFactory.unix();
   }
   
   @Override
   public void generateScript(JavaAppStartScriptGenerationDetails details, Writer destination) {
      try {
          Map<String, String> binding = bindingFactory.transform(details);
          if(details instanceof ExtendedJavaStartScriptGenerationDetails) {
             binding.putAll(details.extendedDetails);
          }
          String generated = generateStartScriptContentFromTemplate(binding);
          destination.write(generated);
      } catch (Exception e) {
          throw new UncheckedIOException(e);
      }
   }
}
