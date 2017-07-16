# gradleapi
Various customizations of existing Gradle functionalities.

## Build Script
In order to use this library in a Gradle build, the following snippet is necessary with the assumption that the library has been pushed to `mavenLocal()`.

```
buildscript {
   repositories {
       mavenLocal()
   }
   dependencies {
       classpath("kkdt.gradle.api:gradleapi:0.1")
   }
}
```

## Extended CreateStartScripts
An extension of the Gradle [CreateStartScripts](https://docs.gradle.org/current/dsl/org.gradle.jvm.application.tasks.CreateStartScripts.html) that allows the user to specify an external template and generic customized bindings. This task is tied to a Unix templated script generator that will always execute; the windows template script generator is optional.

```
task createHelloWorld(type: kkdt.gradle.api.task.ExtendedCreateStartScripts) {
   mainClassName = "kkdt.sample.webboot.HelloWorld"
   applicationName = "helloworld"
   defaultJvmOpts = ["-Dhello=world"]
   outputDir = new File(project.buildDir, 'scripts')
   classpath = jar.outputs.files + project.configurations.runtime
   unixScriptTemplate = file('run/scriptTemplate.txt')
   scriptRelativePath = 'scripts' // default to 'bin' if omitted
   libRelativePath = 'libraries' // default to 'lib' if omitted
   includeWindowsScript = true // default to 'false' if omitted
   extendedDetails = [
      requiredApplicationArguments : "required=name foo=bar",
      project : '\${PROJECT}' // example of how to use an environment variable
   ]
   doLast {
      // just a reminded for myself
      windowsScript.delete()
   }
}
```