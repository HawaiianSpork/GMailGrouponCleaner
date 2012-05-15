# GMail Groupon Mailbox Cleaner

This is a simple daemon that runs from Unix like operating system that will clean out Groupon and other date dependent emails from your mailbox that are older than a day old.

## Building/Installing

Code is built with gradle and just depends on jsvc.

    brew install gradle
    brew install maven
    gradle install

## Running

You need to first configure it with your username and password than start the deamon
  
    build/install/GMailGrouponCleaner/bin/configGMailCleaner  
    build/install/GMailGrouponCleaner/bin/GMailCleaner start  
