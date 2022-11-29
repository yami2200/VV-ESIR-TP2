# Extending PMD

Use XPath to define a new rule for PMD to prevent complex code. The rule should detect the use of three or more nested `if` statements in Java programs so it can detect patterns like the following:

```Java
if (...) {
    ...
    if (...) {
        ...
        if (...) {
            ....
        }
    }

}
```
Notice that the nested `if`s may not be direct children of the outer `if`s. They may be written, for example, inside a `for` loop or any other statement.
Write below the XML definition of your rule.

You can find more information on extending PMD in the following link: https://pmd.github.io/latest/pmd_userdocs_extending_writing_rules_intro.html, as well as help for using `pmd-designer` [here](https://github.com/selabs-ur1/VV-TP2/blob/master/exercises/designer-help.md).

Use your rule with different projects and describe you findings below. See the [instructions](../sujet.md) for suggestions on the projects to use.

## Answer

Comme l'exercice précédent, on créer un ruleset pmd que l'on va executer sur nos projets de tests. On a utilisé pmd designer pour créer une rule qui détecte les ifstatement imbriqués en formant un empilement de 3 ou plus.

### Lang :

On a donc trouvé des ifStatement imbriqués dans toutes ces différentes classes :

| Classes                                             |
|-----------------------------------------------------|
| commons/lang3/ArrayUtils                            |
| commons/lang3/BooleanUtils                          |
| commons/lang3/CharRange                             |
| commons/lang3/CharSet                               |
| commons/lang3/CharSetUtils                          |
| commons/lang3/ClassUtils                            |
| commons/lang3/Conversion                            |
| commons/lang3/LocaleUtils                           |
| commons/lang3/ObjectUtils                           |
| commons/lang3/RandomStringUtils                     |
| commons/lang3/StringUtils                           |
| commons/lang3/builder/CompareToBuilder              |
| commons/lang3/builder/EqualsBuilder                 |
| commons/lang3/builder/HashCodeBuilder               |
| commons/lang3/builder/ToStringStyle                 |
| commons/lang3/math/Fraction                         |
| commons/lang3/math/NumberUtils                      |
| commons/lang3/reflect/ConstructorUtils              |
| commons/lang3/reflect/MemberUtils                   |
| commons/lang3/reflect/MethodUtils                   |
| commons/lang3/reflect/TypeUtils                     |
| commons/lang3/text/StrBuilder                       |
| commons/lang3/text/StrSubstitutor                   |
| commons/lang3/text/StrTokenizer                     |
| commons/lang3/text/WordUtils                        |
| commons/lang3/text/translate/CharSequenceTranslator |
| commons/lang3/text/translate/NumericEntityUnescaper |
| commons/lang3/text/translate/OctalUnescaper         |
| commons/lang3/time/DateUtils                        |
| commons/lang3/time/DurationFormatUtils              |
| commons/lang3/time/FastDatePrinter                  |
| commons/lang3/SystemUtilsTest                       |
| commons/lang3/ArrayUtilsInsertTest                  |

Comme on peut le voir, cette utilisation de if imbriqués restent quand meme assez présentes, dans la plupars des cas il s'agit souvent d'une utilisation excessive de elseif.

### Math :

| Classes                                           |
|---------------------------------------------------|
| commons/math4/core/jdkmath/AccurateMath           |
| commons/math4/neuralnet/MapRanking                |
| commons/math4/neuralnet/twod/NeuronSquareMesh2D   |
| commons/math4/transform/FastCosineTransformerTest |
| commons/math4/transform/FastSineTransformerTest   |
| commons/math4/userguide/genetics/Polygon          |

Pour le cas du projet Apach Maths, on trouve beaucoup moins de cas d'if imbriqués, mais on en trouve quand meme quelques uns.
En revanche, le repository d'apache maths contient du code legacy qui lui comprends beaucoup plus de ces cas. Néanmoins, on peut ne pas tenir compte car par définition le code legacy est un code ancien qui n'est pas forcément propre.