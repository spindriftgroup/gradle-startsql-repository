# gradle-startsql-repository
Gradle wrapper plugin for Oracle Commerce (ATG) startSQLRepository utility


Work in Progress
================

*********  **THIS REPO IS NOT COMPLETE OR RELEASED YET** ************

Usage
=====
Build script snippet for use in all Gradle versions
```$xslt
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "com.spindrift:startsql-repository:1.0.0"
  }
}

apply plugin: "com.spindrift.startsql-repository"
``` 

Custom Tasks
============

- `startSQLRepository` - Executes all configured startSQLRepository commands  
- `startSQLRepository -PconfigName=<name>` - Executes a named startSQLRepository commands  
- `showConfigurations` - Shows startSQLRepository named configurations  

Default Configuration
=====================

- The task names are `startSQLRepository` and `showConfigurations` and are not modifiable


Example configuration overrides
===============================

```$xslt
  startSQLRepository {
    configurations {
      parameters {
        name = 'outputProfileSql'
        repository = "/atg/userprofiling/ProfileAdapterRepository"
        command = 'outputSQL'
      }
      parameters {
        name = 'outputCatalogSql'
        repository = "/atg/commerce/catalog/ProductCatalog"
        modules = ['DCS','MyModule']
        command = 'outputSQLFile'
        outputFile = 'catalog.sql'
      }
      parameters {
        name = 'exportSiteData'
        repository = "/atg/multisite/SiteRepository"
        command = 'export'
        modules = ['MyModule']
        outputFile = 'site-data.xml'
      }
    }
  }
```

Build Notes
===========

1. Maven local installation  
`gradle or gradle publishToMavenlocal`  
2. Publishing to Plugin portal  
`gradle clean build -Prelease=true publishPlugins`  
3. Publishing to Bintray JCenter  
`gradle clean build -Prelease=true bintrayUpload`  

Versions
========

See CHANGELOG

