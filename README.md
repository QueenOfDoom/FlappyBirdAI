# Flappy Bird AI
## Short Description
This was a project to create a Flappy Bird AI in Java.
I used the NEAT Algorithm as a Genetic Algorithm. I
did not optimize the whole code yet. There would be
lots of things to do, starting from implementing
intelligent maths for the neural network computations
and ending at faster representation using libraries
such as OpenGL / OpenCL. Also, a short note: This is 
an older project of mine, and I am yet renovating it,
so please excuse the ugly code / bad conventions.
I am working on fixing those issues.

## Plans
If someone kicks me in the ass, I might implement 
OpenGL and OpenCL in here, so the project will work /
compute a lot faster. The computation times are 
already pretty neat (always assuming highest 
game speed):
- Initial Frame Rendering: 160.5±88.5ms
- Frame Rendering: 12±1ms
- Neural Network Computations: 2±1ms  

Of course if the program runs for a while the times
get bigger since the accumulated data gets bigger
too. The neural network does not stop evolving
at this point, which possibly will get implemented
in the future to prevent an ever-growing chunk of
data (graph). I might include some further 
interesting statistics or actual pictures of the 
neural network in later updates.  
Here are some screenshots of the game and the 
resulting Neural Network:  
![Init Phase](https://github.com/QueenOfDoom/FlappyBirdAI/blob/master/img/InitPhase.png?raw=true)
![Advanced Phase](https://github.com/QueenOfDoom/FlappyBirdAI/blob/master/img/AdvancedPhase.png?raw=true)  
![Generation: 0](https://github.com/QueenOfDoom/FlappyBirdAI/blob/master/img/Gen0.png?raw=true)
![Generation: 2](https://github.com/QueenOfDoom/FlappyBirdAI/blob/master/img/Gen2.png?raw=true)
![Generation: 21](https://github.com/QueenOfDoom/FlappyBirdAI/blob/master/img/Gen21.png?raw=true)
## How to use
I have fixed the "Load Images from the JAR" issue, so
I will start creating releases. You can simply
download the release and either double-click it
or if you want to see the logs, which don't contain
that much of useful data, but things like frame-rates,
whether all resources loaded correctly, basically
debug stuff, then you can launch the file from
the console / terminal using `java -jar <jar-file>`. 
Now to the actual usage. You can adjust
the game speed with keys 1-4. Using the `ESC` key, 
you can skip abort the current generation and with 
`P` you can print the genome graph after the current 
generation has finished, to a file, which is not
yet pretty / well-made, but it serves the purpose.  
I might improve this in later releases.

## Documentation Status
| Class                 | Docs State   |
| :-------------------- | :----------: |
| Util                  | 100%         |
| ImageConfig           | 100%         |
| Bird                  | 100%         |
| Pipe                  | 100%         |
| BirdController        | 0%           |
| CompatibilityDistance | 5%           |
| Connection            | 0%           |
| Counter               | 0%           |
| Evaluator             | 8%           |
| FlappyBirdGenome      | 75%          |
| Genome                | 5%           |
| InnovationGeneration* | 0%           |
| KeyListener           | 15%          |
| Main                  | 0%           |
| Map                   | 55%          |
| Node                  | 0%           |  
| Pipes                 | 0%           |   