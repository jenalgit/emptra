package JsonObject;

import static Utils.GenericUtils.LogMe;

public class Contacts
{
    public String mobile;
    public String email;
    public Address permanent_address;
    public Address current_address;

    public String getFormattedAddress(Address a)
    {
        if(a != null) {
            return a.houseno + ", " +
                    a.street1 + ", \n" +
                    a.street2 + ", " +
                    a.city + ", \n" +
                    a.state + ", " +
                    a.country + ", " +
                    a.postcode;
        }
        else {
            LogMe("Contacts", "Null Address object received");
            return null;
        }
    }
}