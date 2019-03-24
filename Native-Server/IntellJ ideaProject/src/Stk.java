import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Stk extends JFrame {

    private TrayIcon trayIcon;
    private SystemTray tray;
    private JLabel tickicon;
    private JLabel Conn;
    private JLabel stkIcon;
    private JLabel qr;
    private JLabel Scantxt;
    private JLabel directIP;
    private JLabel title;
    private JButton restart;

    public static String web;
    public static String user;
    public static String pass;
    public static JSONObject js;
    private static Socket ms;
    private static ServerSocket mss;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static HttpServer server;

    Stk() throws IOException, WriterException, NotFoundException {
        super("Smart Touch Key");

        System.out.println("creating instance");

        try{
            System.out.println("setting look and feel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.out.println("Unable to set LookAndFeel");
        }
        if(SystemTray.isSupported()){
            System.out.println("system tray supported");
            tray=SystemTray.getSystemTray();

            Image image=Toolkit.getDefaultToolkit().getImage("stk.png");
            ActionListener exitListener=new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting....");
                    System.exit(0);
                }
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Open");
            defaultItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            defaultItem=new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            trayIcon=new TrayIcon(image, "Smart Touch Key", popup);
            trayIcon.setImageAutoSize(true);
        }else{
            System.out.println("system tray not supported");
        }
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2)
                {
                    setVisible(true);
                    setState (JFrame.NORMAL);
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ms!=null && ms.isConnected()){
                    try {
                        int msg=9;
                        new DataOutputStream(ms.getOutputStream()).writeInt(9);

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.out.println("Exiting");
            }
        });
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState()==ICONIFIED){
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                        System.out.println("added to SystemTray");
                    } catch (AWTException ex) {
                        System.out.println("unable to add to tray");
                    }
                }
                if(e.getNewState()==7){
                    try{
                        tray.add(trayIcon);
                        setVisible(false);
                        System.out.println("added to SystemTray");
                    }catch(AWTException ex){
                        System.out.println("unable to add to system tray");
                    }
                }
                if(e.getNewState()==MAXIMIZED_BOTH){
                    tray.remove(trayIcon);
                    setVisible(true);
                    System.out.println("Tray icon removed");
                }
                if(e.getNewState()==NORMAL){
                    tray.remove(trayIcon);
                    setVisible(true);
                    System.out.println("Tray icon removed");
                }
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage("stk.png"));

        //For Getting the IP Address
        String systemipaddress = "";
        try
        {
            //URL url_name = new URL("http://bot.whatismyipaddress.com");
            //BufferedReader sc =
            //new BufferedReader(new InputStreamReader(url_name.openStream()));
            // reads system IPAddress
            //systemipaddress = sc.readLine().trim();
            systemipaddress= InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }

        //Code for QR New
        String qrCodeData = systemipaddress;
        String filePath = "QRCode.png";
        String charset = "UTF-8"; // or "ISO-8859-1"
        Map hintMap = new HashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
        System.out.println("QR Code image created successfully!");

        System.out.println("Data read from QR Code: "
                + readQRCode(filePath, charset, hintMap));

        //Main upper static Content
        JPanel Upper = new JPanel();
        Upper.setLayout(new BoxLayout(Upper, BoxLayout.Y_AXIS));

        ImageIcon stkicon = new ImageIcon("stkimg.png");
        stkIcon = new JLabel(stkicon);
        stkIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        //Line Below is written for resizing the img and the displaying it
        //stkIcon.setIcon(new ImageIcon(new ImageIcon("stkimg.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
        stkIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        Upper.add(stkIcon);

            title = new JLabel("Smart Touch Key!");
            title.setFont(new Font("Courier New", Font.BOLD, 20));
            title.setForeground(new java.awt.Color(50, 58, 69));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            Upper.add(title);
            /*
            JLabel title1 = new JLabel("Welcome to Smart Touch Key!");
            title1.setAlignmentX(Component.CENTER_ALIGNMENT);
            Upper.add(title1);
            JLabel title2 = new JLabel("Welcome to Smart Touch Key!");
            title2.setAlignmentX(Component.CENTER_ALIGNMENT);
            Upper.add(title2);
            */
        ImageIcon qrcode = new ImageIcon("QRCode.png");
        qr = new JLabel(qrcode);
        qr.setAlignmentX(Component.CENTER_ALIGNMENT);
        qr.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3, true));
        Upper.add(qr);

        //Show IP
        directIP = new JLabel("Or Enter this IP: " + systemipaddress);
        directIP.setFont(new Font("", Font.BOLD, 14));
        directIP.setAlignmentX(Component.CENTER_ALIGNMENT);
        directIP.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        Upper.add(directIP);

        Scantxt = new JLabel("Scan the above QR Code in STK Android App to Connect!");
        Scantxt.setFont(new Font("", Font.PLAIN, 12));
        Scantxt.setAlignmentX(Component.CENTER_ALIGNMENT);
        Scantxt.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 8));
        Upper.add(Scantxt);

            //OnConnect this is Visible
            //ImageIcon tick = new ImageIcon("tick.png");
            tickicon = new JLabel();
            tickicon.setIcon(new ImageIcon(new ImageIcon("tick.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
            tickicon.setAlignmentX(Component.CENTER_ALIGNMENT);
            tickicon.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            tickicon.setVisible(false);
            Upper.add(tickicon);

            Conn = new JLabel("Connected!");
            Conn.setFont(new Font("Sans Serif", Font.BOLD, 18));
            Conn.setAlignmentX(Component.CENTER_ALIGNMENT);
            Conn.setVisible(false);
            Upper.add(Conn);

        //Restart Button
        JLabel restarttxt = new JLabel("Facing any Problem? \n Click Restart!");
        restarttxt.setAlignmentX(Component.CENTER_ALIGNMENT);
        restarttxt.setFont(new Font("", Font.PLAIN, 11));
        restarttxt.setBorder(BorderFactory.createEmptyBorder(40, 0, 3, 0));
        restarttxt.setVisible(true);
        Upper.add(restarttxt);
        restart = new JButton("Restart");
        restart.setAlignmentX(Component.CENTER_ALIGNMENT);
        //restart.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        restart.setVisible(true);
        //restart.addActionListener((ActionListener) this);

        restart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1)
                {
                    dispose();
                    server.stop(0);
                    try {
                        new Stk();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (WriterException e1) {
                        e1.printStackTrace();
                    } catch (NotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        Upper.add(restart);



        //Upper.setBounds(100,10,100,100);
        add(Upper);
        //Line below applies Border!
        getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new java.awt.Color(0xFFFFFFFF, true)));

        //setSize(350, 500);
        Upper.setBackground(new java.awt.Color(93, 114, 127, 32));
        setDefaultLookAndFeelDecorated(true);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        //JFrame.DO_NOTHING_ON_CLOSE
        //setDefaultCloseOperation(ExitProgram());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == "restart") {
            this.dispose();
            System.out.println("Hello");
            try {
                dispose();
                new Stk();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriterException e1) {
                e1.printStackTrace();
            } catch (NotFoundException e1) {
                e1.printStackTrace();
            }
        }

    }


    private int ExitProgram()throws IOException{
        if (ms!=null && mss!=null){
            ms.getOutputStream().write(9);
            ms.close();
            mss.close();
        }
        System.out.println("Closing");
        return JFrame.EXIT_ON_CLOSE;
    }

    public static void main(String[] args) throws IOException, NotFoundException, WriterException {
        Stk mainObj = new Stk();
        server = null;
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/GET", new Stk.MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        ServerSocket ss=new ServerSocket(5900);
        Socket s=new Socket();
        System.out.println("Waiting for Android");
        ms=s;
        mss=ss;
        s=ss.accept();
        ms=s;
        mss=ss;
        js=new JSONObject();
        js.put("web","");
        in=new DataInputStream(s.getInputStream());
        out=new DataOutputStream(s.getOutputStream());
        System.out.println("Found Android");
        mainObj.tickicon.setVisible(true);
        mainObj.Conn.setVisible(true);
        mainObj.qr.setVisible(false);
        mainObj.Scantxt.setVisible(false);
        mainObj.stkIcon.setVisible(false);
        mainObj.title.setVisible(false);
        mainObj.directIP.setVisible(false);
        mainObj.pack();
    }

    public static String[] getData() throws IOException{
        if (!js.getString("web").equalsIgnoreCase("")) {
            System.out.println("Connected to" + ms.getRemoteSocketAddress());
            System.out.println("Enter Website URL");
            System.out.println(web);
            out.writeInt(2);
            if (web != null) out.writeUTF(web);
            else out.writeUTF("");
            String dname = in.readUTF();
            System.out.println(dname);
            String domain = in.readUTF();
            System.out.println(domain);
            int x = in.read();
            System.out.println(x);
            user = in.readUTF();
            System.out.println(user);
            pass = in.readUTF();
            System.out.println(pass);
            //HTTPServer.setCred(user,pass);
            js.put("web", "");
            if (user!=null & pass!=null){
                String[] data={user,pass};
            }else return null;
        }
        return null;
    }
    //For QR Code
    public static void createQRCode(String qrCodeData, String filePath,
                                    String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
                .lastIndexOf('.') + 1), new File(filePath));
    }

    public static String readQRCode(String filePath, String charset, Map hintMap)
            throws FileNotFoundException, IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath)))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,
                hintMap);
        return qrCodeResult.getText();
    }

    static boolean AndroComm() throws IOException{
        return true;
    }
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin","*");
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            InputStream in=t.getRequestBody();
            System.out.println("Reading Data");
            byte[] data=new byte[100];
            System.out.println("Before Reading"+data);
            data=in.readAllBytes();
            System.out.println("Reading Complete"+new String(data));
            JSONObject js=new JSONObject(new String(data));
            //System.out.println(js.getString( "msg"));
            Stk.js=js;
            OutputStream os = t.getResponseBody();
            int msg=js.getInt("msg");
            System.out.println("Message is "+msg);
            if (msg==2){
                web=js.getString("web");
                System.out.println("Web is "+web);
            }
            JSONObject j;
            System.out.println("Switching Case");
            switch (msg){
                case 1:
                    System.out.println("In 1st Msg");
                    j=new JSONObject();
                    if (ms.isConnected()) j.put("res","true");
                    else  j.put("res","false");
                    t.sendResponseHeaders(200, j.toString().length());
                    os.write(j.toString().getBytes());
                    //out.writeInt(9);
                    break;
                case 2:
                    System.out.println("In 2nd Msg");
                    j=new JSONObject();
                    j.put("res","true");
                    Stk.getData();
                    //j.put("site",t.getLocalAddress().getHostString());
                    System.out.println("Uname : "+user);
                    System.out.println("Pass : "+pass);
                    if (user.equalsIgnoreCase("N/A") && pass.equalsIgnoreCase("N/A")){
                        j.put("isFound", "false");
                    }
                    else
                    {
                        j.put("isFound", "true");
                        j.put("user",user);
                        j.put("pass",pass);
                    }
                    System.out.println(j.length());
                    t.sendResponseHeaders(200, j.toString().length());
                    os.write(j.toString().getBytes());
                    break;
                default:
                    j=new JSONObject();
                    j.put("res","false");
                    t.sendResponseHeaders(200, j.toString().length());
                    os.write(j.toString().getBytes());
                    System.out.println("Default");
                    break;
                case 9:
                    System.out.println("Disconnected from Android");
                    ms.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ms=mss.accept();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            }
            os.close();
            System.out.println(user);
        }
    }

}
