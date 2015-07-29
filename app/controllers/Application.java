package controllers;

import com.avaje.ebean.Ebean;
import com.ecwid.mailchimp.MailChimpException;
import models.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import play.*;
import play.data.Form;
import play.mvc.*;
import util.misc.Env;
import util.misc.Mailchimp;
import util.misc.Mailchimp.EarlyAccessRegistration;
import models.User.SigninForm;
import models.User.RegisterForm;
import models.User.Role;
import views.html.public_area.signin;
import views.html.public_area.register;
import java.io.File;
import java.io.IOException;

public class Application extends Controller {
    public static final Form<EarlyAccessRegistration> mailchimpForm = Form.form(EarlyAccessRegistration.class);
    public static final Form<SigninForm> signinForm = Form.form(SigninForm.class);
    public static final Form<RegisterForm> registerForm = Form.form(RegisterForm.class);


    public static Result signUp() throws MailChimpException, IOException {
        Form<EarlyAccessRegistration> boundForm = mailchimpForm.bindFromRequest();
        EarlyAccessRegistration info = boundForm.get();
        Mailchimp.subscribe(info.firstName, info.lastName, info.emailAddress);
        return redirect("/thanks");
    }

    public static Result signOut () {
        session().clear();
        return redirect("/");
    }

    public static Result authenticateUser() {
        Form<SigninForm> boundForm = signinForm.bindFromRequest();
        // VALIDATE FORM
        if (boundForm.hasErrors()) {
            Logger.debug("FORM HAS " + boundForm.errors().size() + " ERRORS");
            Logger.debug(String.valueOf(boundForm.errorsAsJson()));
            return badRequest(signin.render(boundForm));
        }
        SigninForm form = boundForm.get();

        Logger.debug(String.valueOf(signinForm.hasErrors()));
//        Logger.debug(form.emailAddress);
//        Logger.debug(form.password);

        try {
            User user = User.findByEmail(form.emailAddress);
            Logger.info(user.toString());

            if (! user.passwordHash.equals(DigestUtils.sha512Hex(form.password + Env.get(Env.Variable.PWD_SALT)))) {
                flash("error", "Wrong password");
                return redirect("/signin");
            }
            if (user.accountLocked) {
                flash("error", "Your account is locked");
                return redirect("/signin");
            }


            session("id", String.valueOf(user.id));
            session("role", user.role.toString());
            session("firstName", user.firstName);
            session("email", user.emailAddress);

            user.setLastLogin(new DateTime());
            Ebean.save(user);

            if (user.role.equals(Role.ADMIN)) {
                return redirect("/admin");
            }
            if (user.role.equals(Role.APPLICANT)) {
                return redirect("/applicant");
            }
            if (user.role.equals(Role.RECRUITER)) {
                return redirect("/recruiter");
            }

            return redirect("/signin");



        }
        catch (NullPointerException e) {
            flash("error", "Username or password incorrect");
            return redirect("/signin");
        }



        // CHECK USER ROLE

        // STORE USER IN SESSION


    }

    public static Result registerUser() {
        Form<RegisterForm> boundForm = registerForm.bindFromRequest();
        // VALIDATE FORM
        if (boundForm.hasErrors()) {
            Logger.debug("FORM HAS " + boundForm.errors().size() + " ERRORS");
            Logger.debug(String.valueOf(boundForm.errorsAsJson()));
            return badRequest(register.render(boundForm));
        }
        RegisterForm form = boundForm.get();
        User.registerUser(form.emailAddress, form.password, form.role);
        return redirect("/");
    }

    public static Result upload() {
        File file = request().body().asRaw().asFile();
        Logger.info(file.getName());
        return ok("File uploaded");
    }






}
