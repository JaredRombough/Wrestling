# Wrestling
Wrestling booking simulation




HOW TO BUILD WITH NETBEANS

(tested with Netbeans 8.1, use default settings unless otherwise noted)

Team -> Remote -> Clone

Repository URL: https://github.com/JaredRombough/Wrestling.git

File -> New Project

Select JavaFX -> JavaFX Project with Existing Sources

Source Package Folders -> Add Folder -> wrestling\src


At this point you should have the project files visible in the "Projects" pane


Download the following JAR files:


Kryo

https://oss.sonatype.org/content/repositories/snapshots/com/esotericsoftware/kryo/

kryo-5.0.0-SNAPSHOT

minlog-1.3.0

objenesis-2.6

reflectasm-1.11.6


Apache Log4j 2

https://logging.apache.org/log4j/2.0/download.html

log4j-api-2.11.1

log4j-core-2.11.1


Appache Commons Lang

https://commons.apache.org/proper/commons-lang/download_lang.cgi

commons-lang3-3.8.1

Add the above JAR files to the project by right clicking Libraries -> Add JAR/Folder

That's it, you should now be able to compile and run the project.






FILE STRUCTURE

as of 2019-01-14

/model
- game models

/model/controller

GameController - The main game loop and high level game functions

PromotionController - Most AI goes here

/model/manager
- Classes that control lists of models (titles, workers, contracts, etc.)

/model/modelView
- main complex models used for game functions

/model/segmentEnum
- Types for segments and models

/view/
- ui
- typically each component is an fxml file with an accompanying controller java class
