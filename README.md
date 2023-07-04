# text-format

## Description

Programmatically build formatted texts, mainly properly indent programming language
sources.

## Usage

Import builder functions

```
import static maamissiniva.text.format.TextDocShortcuts.*;
```

### Document construction

Use functions to combine text fragments.

See [shortcuts](https://javadoc.io/doc/io.github.maamissiniva/maamissiniva-text-format/latest/maamissiniva/text/format/TextDocShortCuts.html) for details.

Vertical concatenation:

```
vcat(txt("public static class {"),
     emptySpace,
     indent(4, generateClassBodyDoc()),
     emptySpace,
     txt("}"));
```

Horizontal concatenation:

```
hcat(txt("public"), space, txt("static"), space, txt("class"))
```

## Rendering

Either 

```
t.render();
```

or

```
t.toString();
```

