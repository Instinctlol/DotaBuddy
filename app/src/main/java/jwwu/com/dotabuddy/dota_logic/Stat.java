package jwwu.com.dotabuddy.dota_logic;

/**
 * Created by Instinctlol on 28.02.2016.
 */
public class Stat {
    private String name;                                    //e.g. "Damage"
    private String value;                                  //e.g. 100

    public Stat(String name, String value) {
        this.name=name;
        this.value=value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
