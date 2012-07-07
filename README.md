# GMail Groupon Mailbox Cleaner

This is a simple daemon that runs from Unix like operating system that will clean out Groupon and other date dependent emails from your mailbox that are older than a day old.

## Building/Installing

To build and install the code on a Mac run:

    brew install jsvc
    brew install gradle
    brew install maven
    gradle install

## Running

You need to first configure it with your username and password than start the deamon
 
    export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/Current/     
    buil/dinstall/GMailGrouponCleaner/bin/configGMailCleaner  
    build/install/GMailGrouponCleaner/bin/GMailCleaner start  
