grammar Cli;

statement returns [ru.iisuslik.cli.Statement value]
    :   SPLIT* (a=assignment {$value = $a.value;}
        | c=commands {$value = new ru.iisuslik.cli.Commands($c.list);}) SPLIT*
    ;

assignment returns [ru.iisuslik.cli.Assignment value]
    :   w=word '=' a=arg {$value = new ru.iisuslik.cli.Assignment($w.text, $a.value);}
    ;

commands returns [java.util.List<ru.iisuslik.cli.Command> list]
    :   {$list = new java.util.ArrayList<>();}
        c=command {$list.add($c.value);}
        (SPLIT+ '|' SPLIT+ c=command {$list.add($c.value);})*
    ;

command returns [ru.iisuslik.cli.Command value]
    :   w=word a=args {$value = ru.iisuslik.cli.StatementParser.build($w.text, $a.list);}
    ;

args returns [java.util.List<String> list]
    :   {$list = new java.util.ArrayList<>();}
        (SPLIT+ a=arg {$list.add($a.value);})*

    ;


arg returns [String value]
    :   w=word {$value = $w.text;}
        | '"' s2=q2string '"' {$value = $s2.text;}
        | '\'' s1=q1string '\'' {$value = $s1.text;}
    ;

word
    :   (LETTER | DIGIT | '.' | '-' | '/')+
    ;

string
    : string_letter+
    ;

q1string
    : (string_letter | '"' | '$')+
    ;

q2string
    : (string_letter | '\'')+
    ;

string_letter
    : LETTER | DIGIT | '.' | '-' | ' ' | '!' | '\t' | '|'
    ;

DIGIT
    : '0'..'9'
    ;
LETTER: ('a'..'z') | ('A'..'Z');

SPLIT: ' ';
