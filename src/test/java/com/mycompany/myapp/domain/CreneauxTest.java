package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CreneauxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Creneaux.class);
        Creneaux creneaux1 = new Creneaux();
        creneaux1.setId("id1");
        Creneaux creneaux2 = new Creneaux();
        creneaux2.setId(creneaux1.getId());
        assertThat(creneaux1).isEqualTo(creneaux2);
        creneaux2.setId("id2");
        assertThat(creneaux1).isNotEqualTo(creneaux2);
        creneaux1.setId(null);
        assertThat(creneaux1).isNotEqualTo(creneaux2);
    }
}
