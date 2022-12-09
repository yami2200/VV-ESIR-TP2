# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset. Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

Pour réaliser cet exercice, nous avons choisi le projet [Apache Commons Lang](https://github.com/apache/commons-lang). On a ensuite exécuté PMD avec un ruleset contenu dans rulepmd.xml (ensemble des rulesets de codestyle.xml et errorprone.xml).

#### True positive issue: 
Avec l'analyse PMD, on peut retrouver une erreur "AppendCharacterWithChar" à la ligne "code/Exercise2/commons-lang-master/src/main/java/org/apache/commons/lang3/ClassUtils.java:1516" par exemple. Il s'agit d'une petite issue sans trop d'importance mais qui cause une petite perte de performance et qui peut être corrigé facilement. Elle est due à l'utilisation de la méthode append() avec un char stocké dans une String. On peut donc la corriger en utilisant la méthode append() avec un Char directement.

Au lieu d'avoir : 
```java
s.append("L");
```

On peut avoir : 
```java
s.append('L');
```

#### False positive issue : 
PMD a trouvé une erreur "CompareObjectsWithEquals" à la ligne commons-lang-master\src\main\java\org\apache\commons\lang3\AnnotationUtils.java:114.

```java
    private static boolean memberEquals(final Class<?> type, final Object o1, final Object o2) {
        ---> if (o1 == o2) {  <-------------------- HERE
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (type.isArray()) {
            return arrayMemberEquals(type.getComponentType(), o1, o2);
        }
        if (type.isAnnotation()) {
            return equals((Annotation) o1, (Annotation) o2);
        }
        return o1.equals(o2);
    }
```

L'erreur détectée n'en est pas vraiment une étant donné que l'utilisation de l'opérateur "==" au lieu de .equals() est souhaité dans cette situation.
L'erreur soulevée par PMD vise à éviter d'utiliser par mégarde l'opérateur == entre deux objets (qui comparent leur adresse de référence et non pas leur valeur). Or dans cet exemple, on souhaite d'abord vérifier si nous avons affaire aux deux mêmes instances, puis on finit à la dernière ligne par utiliser le .equals().