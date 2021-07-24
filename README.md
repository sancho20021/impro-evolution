# impro-evolution
## Tips to download and open project
Java 11 or newer Java version is required.

If you use Intellij IDEA, you can follow these steps to open this project on your computer:

1. Clone the repository using [this link](https://github.com/sancho20021/impro-evolution.git)
2. In Intellij IDEA, select File -> New -> Project from Existing Sources.
Specify the path to the cloned repository. Choose *Import project from external model* -> Maven -> Finish
   
## Usage instruction
- You can start playing with impro-evolution music synthesizer by running [main class](https://github.com/sancho20021/impro-evolution/blob/main/src/main/java/examples/Main.java)
  - Type commands in standard input. All commands can be shown by typing *help*
  - Every music session if finished via *stop* command is saved in [this directory](https://github.com/sancho20021/impro-evolution/tree/main/src/main/resources/data)
- Play your saved session by writing and executing new test method in [PlaySaved class](https://github.com/sancho20021/impro-evolution/blob/main/src/main/java/examples/PlaySaved.java). 
- Continue to improvise from where you left off using [ContinueSaved class](https://github.com/sancho20021/impro-evolution/blob/main/src/main/java/examples/ContinueSaved.java)
