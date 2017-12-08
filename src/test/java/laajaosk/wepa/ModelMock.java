package laajaosk.wepa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.Model;

public class ModelMock implements Model {

    private Map<String, Object> attributes;

    public ModelMock() {
        this.attributes = new HashMap<>();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Model addAttribute(String string, Object o) {
        if (this.attributes.keySet().contains(string)) {
            this.attributes.replace(string, o);
        } else {
            this.attributes.put(string, o);
        }
        return this;
    }

    @Override
    public Model addAttribute(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model addAllAttributes(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model addAllAttributes(Map<String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model mergeAttributes(Map<String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAttribute(String string) {
        for (String string1 : this.attributes.keySet()) {
            if (string1.equals(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> asMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
