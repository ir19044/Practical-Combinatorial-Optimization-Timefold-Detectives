package lu.df.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Getter @Setter
public class Thief { private int id; private String name;
    public Thief() { }
    public Thief(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thief thief = (Thief) o;
        return id == thief.id && Objects.equals(name, thief.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
