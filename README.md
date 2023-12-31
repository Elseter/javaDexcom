# javaDexcom
A java port of the pydexcom API created by gagebenne and hosted at https://github.com/gagebenne/pydexcom

## What is this Project
This is a Java port of the pydexcom API, an API that reaches out to the Dexcom Share servers and requests Dexcom CGM data. It was designed to give potential developers access to their own Dexcom CGM data in a format that would be conducive to Android app development. If you do not have a substantial preference for java, I highly recommend using the original python version of this project by gagebenne linked above. 
Requirements:
- Enabled the Dexcom Share service on your account and have shared it with at least one follower
- Then, use your credentials (not the followers) with the pydexcom or javadexcom packages
- For testing, I recommend downloading the .java files from this project and compiling them within your chosen IDE

## Beta
This is still a very early build and requires a great deal of cleaning. In particular, error handling is almost entirely missing
Please be very careful in using this service. It is NOT a replacement for your Dexcom app.
