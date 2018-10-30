# Url Counter #

Read the content in HTML format from URL: https://en.wikipedia.org/wiki/Europe
Afterwards each link in that article is followed up, reads again the content from the link found and stores all of the URLs found in the content

The processing of each url is done in parallel using Akka actors model running AkkaApplication
and using usual runnable tasks execution with ClassicApplication

The result is saved in a file 

## Prerequisites
- JDK8

## Build Steps
- `./gradlew clean build`

## Run Steps
- `./gradlew clean runActors`
- `./gradlew clean runClassic`
