/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laajaosk.wepa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author oce
 */
public class RedirectAttributesMock implements RedirectAttributes {
    
    private Map<String, Object> map;

    public RedirectAttributesMock() {
        this.map = new HashMap<>();
    }
    
    

    @Override
    public RedirectAttributes addAttribute(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RedirectAttributes addAttribute(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RedirectAttributes addAllAttributes(Collection<?> clctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RedirectAttributes mergeAttributes(Map<String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RedirectAttributes addFlashAttribute(String string, Object o) {
        if (o == null && this.map.containsKey(string)) {
            this.map.remove(string);
            return this;
        }
        if (this.map.containsKey(string)) {
            this.map.replace(string, o);
        } else {
            this.map.put(string, o);
        }
        return this;
    }

    @Override
    public RedirectAttributes addFlashAttribute(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, ?> getFlashAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Model addAllAttributes(Map<String, ?> map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAttribute(String string) {
        if (this.map.containsKey(string)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> asMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
