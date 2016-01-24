package wol;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Handles server list
 */
public class ApiregClient extends StringTCPClient {

    private String nick;
    private String email;
    private String password;
    private String apgar;
    private String birthMonth;
    private int birthDay;
    private int birthYear;
    private int langCode;
    private int age;
    private String sku;
    private String ver;
    private String serial;
    private String sysid;

    private int code = 0;
    private String message;

    private ApiregServer apireg;

    /**
    * What the client is currently requesting
    */
    private String request;

    /**
     * Get nickname
     * 
     * @return          current nickname
     */
    public String getNick() {
        return this.nick;
    }

    /**
     * Get Password
     * 
     * @return          current Password
     */
    public String getPass() {
        return this.password;
    }

    public String getApgar() {
        return this.apgar;
    }

    public Integer getBirthYear() {
        return this.birthYear;
    }

    public String getBirthMonth() {
        return this.birthMonth;
    }

    public Integer getBirthDay() {
        return this.birthDay;
    }

    public String getEmail() {
        return this.email;
    }

    // message from MySQL: account created, failed etc.
    public String getMessage() {
        return this.message;
    }

    // HRESULT code from MySQL.
    // tells client if creation was successful or not.
    public Integer getCode() {
        return this.code;
    }

    public void setMessage(String newMessage) {
        this.message = newMessage;
    }

    public void setCode(Integer newCode) {
        this.code = newCode;
    }

    public void setAge(Integer newAge) {
        this.age = newAge;
    }

    public Integer getAge() {
        return this.age;
    }
    
    protected ApiregClient(SocketChannel channel, Selector selector, ApiregServer server) {
        super(channel, selector);
        this.apireg = server;
    }

    public void putString(String message) {
        System.out.println(address + ":" + port + " <- " + message);
        super.putString(message + "\r");
    }

    protected void onString(String message) {
        System.out.println(address + ":" + port + " -> " + message);
        String[] command = message.split("=");
        if (command[0].equalsIgnoreCase("request")) {
            this.request = command[1];
        }

        else if (command[0].equalsIgnoreCase("Email")) {
            this.email = command[1];
        }

        else if (command[0].equalsIgnoreCase("Serial")) {
            this.serial = command[1];
        }

        else if (command[0].equalsIgnoreCase("SysID")) {
            this.sysid = command[1];
        }

        else if (command[0].equalsIgnoreCase("NewNick")) {
            this.nick = command[1];
        }

        else if (command[0].equalsIgnoreCase("NewPass")) {
            this.password = command[1];
            this.apgar = encode(command[1]);
        }

        else if (command[0].equalsIgnoreCase("Bmonth")) {
            this.birthMonth = command[1];
        }

        else if (command[0].equalsIgnoreCase("Bday")) {
            this.birthDay = Integer.valueOf(command[1]);
        }

        else if (command[0].equalsIgnoreCase("Byear")) {
            this.birthYear = Integer.valueOf(command[1]);
        }

        else if (command[0].equalsIgnoreCase("END")) {
            if (this.request.equals("apireg_ageverify")) {
                apireg.onAgeVerify(this);
            } else if (this.request.equals("apireg_getnick")) {
                apireg.onGetNick(this);
            }
        }
    }

    private String encode(String pass) {
      String lookup = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./";
      char[] out = new char[8];

      for (int i = 0; i <= 7; i++) {
        int left  = pass.charAt(i);
        int right = (i == 0) ? 0 : pass.charAt(8 - i);
        int x = ((left & 1) > 0 ? (left << 1) & right : left ^ right) & 63;
        out[i] = lookup.charAt(x);

      }
      return String.valueOf(out);
    }

    protected void onConnect() {
        System.out.println(address + ":" + port + " connected to ApiregServer");
    }

    protected void onDisconnect() {
        System.out.println(address + ":" + port + " disconnected from ApiregServer");
    }

}
