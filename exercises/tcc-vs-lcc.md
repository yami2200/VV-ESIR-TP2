# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

## Answer

La valeur de TCC et LCC peuvent être calculées respectivement avec la manière suivante :

Soit :

NP = N*(N-1)/2 avec N le nombre de méthodes

NDC = Le nombre de connections directes

NIC = Le nombre de connections indirectes

TCC = NDC/NP
LCC = (NDC + NIC)/NP

Il faut donc que NIC = 0, donc qu'il n'y ait pas de connections indirectes dans la classe.

Cet exemple illustre cette propriété :

    public class Main {
    String variable = "";
    
        public void method1() {
            variable = "Raïs";
        }
    
        public void method2() {
            method2();
        }
    
    }

    //N = 2
    //NP = 2
    //NDC = 1
    //NIC = 0
    //TCC = 1/2
    //LCC = 1/2

On n'a pas de liaison indirecte, on a donc TCC = LCC = 1/2.

D'après les relations données ci-dessus, il est impossible que LCC < TCC car LCC = TCC + NIC/NP. 

En effet, LCC < TCC <=> TCC + NIC/NP < TCC <=> NIC/NP < 0, ce qui est impossible car NIC et NP sont des nombres possitifs.