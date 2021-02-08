package gonevas.com.model;

import java.util.ArrayList;
import java.util.List;

public class Variable {
    public String variation_id = "";
    public String regular_price = "";
    public String selling_price = "";
    public String description = "";
    public String weight = "";
    public List<AttributeProperty> attributes = new ArrayList<>();
    public Dimension dimensions = new Dimension();

}
/*
*
*


[variation_id] => 15439
[attributes] => Array
    (
        [0] => Array
            (
                [name] => taille
                [value] => S
            )

    )

[regular_price] => 800000
[selling_price] => 780000
[weight] =>
[dimensions] => Array
    (
        [length] =>
        [width] =>
        [height] =>
    )

[description] =>
)

*
*
*
*
*
*
*
*
*
* */