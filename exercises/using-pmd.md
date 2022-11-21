# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

Pour réaliser cet exercice, nous avons choisi le projet [Apache Commons Lang](https://github.com/apache/commons-lang). On a ensuite exécuté PMD avec un ruleset contenu dans rulepmd.xml (ensemble des rulesets de codestyle.xml et errorprone.xml).

True positive issue: 
Avec l'analyse PMD, on peut retrouver une erreur "AppendCharacterWithChar" à la ligne "code/Exercise2/commons-lang-master/src/main/java/org/apache/commons/lang3/ClassUtils.java:1516" par exemple. Il s'agit d'une petite issue sans trop d'importance mais qui cause une petite perte de performance et qui peut être corrigé facilement. Elle est due à l'utilisation de la méthode append() avec un char stocké dans une String. On peut donc la corriger en utilisant la méthode append() avec un Char directement.

Au lieu d'avoir : 
```java
s.append("L");
```

On peut avoir : 
```java
s.append('L');
```

False positive issue : 
PMD a trouvé une erreur "LongVariable" à la ligne /code/Exercise2/commons-lang-master/src/test/java/org/apache/commons/lang3/RangeTest.java:150. Cette érreur n'a pas d'impact sur le fonctionnement et le déroulement du code de test. L'erreur spécifie que le nom de la variable "derivedComparableA" est trop long. Nous ne pensons pas que corriger cette erreur ait un interet étant donné que le test fonctionnera de la meme manière et le code ne sera pas plus lisible/compréhensible pour autant.