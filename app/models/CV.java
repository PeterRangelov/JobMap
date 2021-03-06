package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import models.Address.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity @Embeddable @Data @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(callSuper=false)
public class CV extends Model {

    @Column(length = 20) @NotNull @Constraints.Required
    public String firstName;

    @Column(length = 30) @NotNull @Constraints.Required
    public String lastName;

    public String photoUrl;

    public String summary;

    @Constraints.Email @Column(length = 50) @NotNull @Constraints.Required
    public String email;

    @Column(length = 12)
    public String phone;

    @Column(length = 50)
    public String street;

    @Column(length = 25)
    public String city;

    @Enumerated(value= EnumType.STRING)
    public State state;

    @Column(length = 5) @Constraints.Pattern(value = "^\\d{5}$")
    public String zipcode;

    @Column(length = 50) @NotNull @Constraints.Required
    public String residencyProgram;

    @NotNull @Constraints.Required
    public Date residencyGraduation;

    @Column(length = 50) @NotNull @Constraints.Required
    public String medSchool;

    @NotNull @Constraints.Required
    public Date medSchoolGraduation;

    public String CVlink;
    
    public static Finder<Long, CV> find = new Finder<Long, CV>(Long.class, CV.class);
}
