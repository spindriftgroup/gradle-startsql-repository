# gradle-startsql-repository
Gradle wrapper plugin for Oracle Commerce (ATG) startSQLRepository utility

Pre-Requisites
==============

- Oracle Commerce (ATG) installation - For build, test and execution  
- ATG_HOME environment variable set  
- A localconfig or server module pre-configured with relevant datasource details for import/export

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

Task Shortcuts
==============

- `startSQLRepository` - `sSQLR`  
- `showConfigurations` - `sC` 

Default Configuration
=====================

- The task names are `startSQLRepository` and `showConfigurations` and are not modifiable  
- Supported commands are `outputSQL`,`outputSQLFile`,`export`,`exportAll`,`exportRepositories`  
- Most optional configuration is done in a sub-closure `options {}`.  
- There is basic validation on certain command an option configuration. Any other is delegated to the utility itself.  

Example configuration overrides
===============================

```$xslt
  startSQLRepository {
    configurations {
      parameters {
        repository = "/atg/userprofiling/ProfileAdapterRepository"
        command = 'outputSQL'
      }
      parameters {
        name = 'outputCatalogSql'
        repository = "/atg/commerce/catalog/ProductCatalog"
        modules = ['DCS','MyModule']
        command = 'outputSQLFile'
        file = 'catalog.sql'
      }
      parameters {
        name = 'exportMultiRepoData'
        repositories = ["/atg/multisite/SiteRepository","/atg/commerce/catalog/ProductCatalog"]
        command = 'exportRepositories'
        modules = ['MyModule']
        file = 'multi-repo-data.xml'
      }
      parameters {
        name = 'exportCatalogTypes'
        repository = "/atg/commerce/catalog/ProductCatalog"
        command = 'export'
        modules = ['MyModule']
        itemTypes= ['category','product','sku']
        file = 'catalog-type-data.xml'
      }
      parameters {
        name = 'exportAllCatalogData'
        repository = "/atg/commerce/catalog/ProductCatalog"
        command = 'exportAll'
        modules = ['MyModule']
        file = 'catalog-all-data.xml'
        options {
          database = 'oracle'
          noTransaction true
          debug true
          skipReferences true
        }
      }
      parameters {
        name = 'importAllCatalogData'
        repository = "/atg/commerce/catalog/ProductCatalog"
        command = 'import'
        modules = ['MyModule']
        file = 'catalog-all-data.xml'
        options {
          database = 'oracle'
          noTransaction true
          debug true
          skipReferences true
        }
      }
      parameters {
        name = 'importCatalogVersionedData'
        repository = "/atg/commerce/catalog/ProductCatalog"
        server = 'MyServer'
        command = 'import'
        modules = ['MyModule']
        file = 'catalog-versioned-data.xml'
        options {
          workspace 'ImportInitialCatalogData'
          user 'myName'
          comment 'Initial catalog import'
        }
      }
      parameters {
        name = 'importCatalogVersionedData'
        repository = "/atg/commerce/catalog/ProductCatalog"
        server = 'MyServer'
        command = 'import'
        modules = ['MyModule']
        file = 'catalog-versioned-data.xml'
        options {
          project 'ImportCatalogData'
          user 'myName'
          comment 'Some other catalog import'
          workflow 'myCustomWorkflow'
        }
      }
    }
  }
```

Example `showConfigurations` task output
=======================================
```aidl
:showConfigurations
----------------------------------------
startSQLRepository configurations
----------------------------------------

name: outputContentSql
\--- repository: /atg/content/ContentManagementRepository
\--- command: outputSQL
\--- modules: [ContentMgmt]
\--- CLI args: startSQLRepository -m ContentMgmt -repository /atg/content/ContentManagementRepository -outputSQL

name: outputProfileSql
\--- repository: /atg/userprofiling/ProfileAdapterRepository
\--- command: outputSQL
\--- modules: [DCS]
\--- server: original
\--- CLI args: startSQLRepository -m DCS -s original -repository /atg/userprofiling/ProfileAdapterRepository -outputSQL

un-named: 2
\--- repository: /atg/userprofiling/ProfileAdapterRepository
\--- command: outputSQL
\--- CLI args: startSQLRepository -repository /atg/userprofiling/ProfileAdapterRepository -outputSQL

name: outputProfileSqlToFile
\--- repository: /atg/userprofiling/ProfileAdapterRepository
\--- command: outputSQLFile
\--- modules: [DCS]
\--- server: original
\--- file i-o: /tmp/kak.sql
\--- CLI args: startSQLRepository -m DCS -s original -repository /atg/userprofiling/ProfileAdapterRepository -outputSQLFile /tmp/kak.sql

```

Build Notes
===========

1. Maven local installation  
`gradle or gradle publishToMavenlocal`  
2. Publishing to Plugin portal  
`gradle clean build -Prelease=true publishPlugins`  
3. Publishing to Bintray JCenter  
`gradle clean build -Prelease=true bintrayUpload`  

**_Note_** To run the functional tests a local ATG installation is required with ATG_HOME env parameter set.

Versions
========

See CHANGELOG

