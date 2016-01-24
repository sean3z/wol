package wol;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

/**
 * Listens for server list clients
 */
public class ApiregServer extends TCPServer {

    /**
     * Client's Database
     */
    private ClientDatabase db;

    protected ApiregServer(InetAddress address, int port, Selector selector) throws IOException {
        super(address, port, selector);
        System.out.println("ApiregServer listening on " + address + ":" + port);
    }

    protected void onAccept(SocketChannel clientChannel) {
        ApiregClient client = new ApiregClient(clientChannel, selector, this);
        client.onConnect();
        this.db = new ClientDatabase(client);
    }

    /**
     * Write a server reply to client without any params
     * 
     * @param client    target client
     * @param message      non-terminated message
     */
    protected void putReply(ApiregClient client, String message) {
       client.putString(message);
    }

    public int getAge(Date dateOfBirth) {
        if (dateOfBirth == null) {
            return 10;
        }

        Calendar now = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        if (dob.after(now)) {
            // return underage user if adds date in future
            return 10;
        }

        int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    protected void onAgeVerify(ApiregClient client) {
        Date date = null;
        String string = client.getBirthMonth() +" "+ client.getBirthDay().toString() +", "+ client.getBirthYear().toString();
        try {
            date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(string);
        } catch(java.text.ParseException e){
            System.out.println("Unable to parse date:" + string);
        }

        Integer age = this.getAge(date);
        client.setAge(age);
        
        putReply(client, "HRESULT=0\nAge="+ age.toString() +"\nEND");
        client.disconnect();
    }

    protected void onGetNick(ApiregClient client) {
        db.insertUser(client);
        putReply(client, "HRESULT="+ client.getCode() +"\nMessage="+ client.getMessage() +"\nNewNick="+ client.getNick() +"\nNewPass="+ client.getPass() +"\nAge="+ client.getAge() +"\nConsent=1\nEND");
        client.disconnect();
    }

}