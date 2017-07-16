/** 
 * Copyright (C) 2017 thinh ho
 * This file is part of 'gradleapi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package kkdt.gradle.api.task.scripts

import org.gradle.api.Transformer
import org.gradle.api.internal.plugins.StartScriptTemplateBindingFactory
import org.gradle.api.resources.TextResource
import org.gradle.internal.io.IoUtils
import org.gradle.jvm.application.scripts.JavaAppStartScriptGenerationDetails
import org.gradle.jvm.application.scripts.TemplateBasedScriptGenerator
import org.gradle.util.TextUtil

import groovy.text.SimpleTemplateEngine
import groovy.text.Template

/**
 * <p>
 * Unix-based start script generator that is modeled off the Gradle's
 * <code>DefaultTemplateBasedStartScriptGenerator</code> but takes into account
 * the additional details/bindings when invoking Groovy's <code>SimpleTemplateEngine</code>.
 * </p>
 * 
 * @author thinh ho
 *
 */
class ExternalTemplateStartScriptGenerator implements TemplateBasedScriptGenerator {
   TextResource template;
   String lineSeparator;
   Transformer<Map<String, String>, JavaAppStartScriptGenerationDetails> bindingFactory;
   
   @Override
   public TextResource getTemplate() {
      return template;
   }

   @Override
   public void setTemplate(TextResource template) {
      this.template = template;
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
          throw new IOException(e);
      }
   }
   
   private String generateStartScriptContentFromTemplate(final Map<String, String> binding) {
      return IoUtils.get(template.asReader(), new Transformer<String, Reader>() {
         @Override
         public String transform(Reader reader) {
            try {
               SimpleTemplateEngine engine = new SimpleTemplateEngine();
               Template template = engine.createTemplate(reader);
               String output = template.make(binding).toString();
               return TextUtil.convertLineSeparators(output, lineSeparator);
            } catch (IOException e) {
               throw e;
            }
         }
      });
   }
}
