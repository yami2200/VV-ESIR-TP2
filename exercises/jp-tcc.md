# Class cohesion with JavaParser

With the help of JavaParser implement a program that computes the Tight Class Cohesion (TCC) for each class in a given Java project. The program should take as input the path to the source code of the project. It should produce a report in the format of your choice (TXT, CSV, Markdown, HTML, etc.) containing a table showing for each class: the package, name and TCC value. 
Your application should also produce a histogram showing the distribution of CC values in the project. Compare the histogram of two or more projects.
Finally, your application should also produce the dependency graph of each class (cf. example [here](https://people.irisa.fr/Benoit.Combemale/pub/course/vv/vv-textbook-v0.1.pdf#cohesion-graph)). The graph should be written using the [GraphViz DOT format](https://www.graphviz.org/)

Ignore inherited members to compute TCC of a class.

Include in this repository the code of your application. Remove all unnecessary files like compiled binaries. Do include the reports and plots you obtained from different projects. See the [instructions](../sujet.md) for suggestions on the projects to use.

You may use [javaparser-starter](../code/javaparser-starter) as a starting point.

# Answer

On a calculé les TCC selon ces formules :

NP = N * (N-1)/2  (Avec N le nombre de méthodes de la classe)

DC = direct connections

TCC = DC/NP

En utilisant les explications de ce site : https://www.aivosto.com/project/help/pm-oo-cohesion.html.


## All reports made with the application :

- [CLI](./../code/Exercise5/report/Cli/report.md)
- [Collections](./../code/Exercise5/report/Collections/report.md)
- [Lang](./../code/Exercise5/report/Lang/report.md)
- [Maths](./../code/Exercise5/report/Maths/report.md)