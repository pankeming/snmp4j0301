package org.snmp4j.test;

import com.sun.scenario.effect.impl.prism.PrImage;
import org.snmp4j.*;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.net.UnknownHostException;

public class SnmpTrapRecevier implements CommandResponder {

    private String username1 = "user1";
    private String username2 = "user2";
    private String username3 = "user3";
    private String username4 = "user4";
    private String authPassword = "password1";
    private String privPassword = "password2";

    private MultiThreadedMessageDispatcher dispatcher;
    private Snmp snmp = null;
    private Address listenAddress;
    private ThreadPool threadPool;

    public SnmpTrapRecevier(){

    }
    private void init() throws UnknownHostException, IOException{
        //创建接受SnmpTrap的线程池，参数：线程名称和线程数
        threadPool = ThreadPool.create("Trap",2);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool,new MessageDispatcherImpl());
        //监听端的IP地址和监听端口号
        listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress","udp:127.0.0.1/162"));

        TransportMapping<?> transport;
        if(listenAddress instanceof UdpAddress) {
            transport = new DefaultUdpTransportMapping((UdpAddress)listenAddress);
        }
        else{
            transport = new DefaultTcpTransportMapping((TcpAddress)listenAddress);
        }
        snmp = new Snmp(dispatcher,transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());

        USM usm = new USM(SecurityProtocols.getInstance(),new OctetString(MPv3.createLocalEngineID()),0);

//        SecurityModels.getInstance().addSecurityModel(usm);
//        // 添加安全协议,如果没有发过来的消息没有身份认证,可以跳过此段代码
//        SecurityProtocols.getInstance().addDefaultProtocols();
//        // 创建和添加用户
//        OctetString userName1 = new OctetString(username1);
//        OctetString userName2 = new OctetString(username2);
//        //OctetString userName3 = new OctetString(username3);
//        //OctetString userName4 = new OctetString(username4);
//        OctetString authPass = new OctetString(authPassword);
//        OctetString privPass = new OctetString("privPassword");
//        UsmUser usmUser1 = new UsmUser(userName1, AuthMD5.ID, authPass, PrivDES.ID, privPass);
//        UsmUser usmUser2 = new UsmUser(userName2, AuthMD5.ID, authPass, PrivDES.ID, privPass);
//        //UsmUser usmUser3 = new UsmUser(userName3, AuthMD5.ID, authPass, PrivDES.ID, privPass);
//        //UsmUser usmUser4 = new UsmUser(userName4, AuthMD5.ID, authPass, PrivDES.ID, privPass);
//        //因为接受的Trap可能来自不同的主机，主机的Snmp v3加密认证密码都不一样，所以根据加密的名称，来添加认证信息UsmUser。
//        //添加了加密认证信息的便可以接收来自发送端的信息。
//        UsmUserEntry userEnty1 = new UsmUserEntry(userName1,usmUser1);
//        UsmUserEntry userEnty2 = new UsmUserEntry(userName2,usmUser2);
//        //UsmUserEntry userEnty3 = new UsmUserEntry(userName3,usmUser3);
//        //UsmUserEntry userEnty4 = new UsmUserEntry(userName4,usmUser4);
//        UsmUserTable userTable = snmp.getUSM().getUserTable();
//        // 添加其他用户
//        userTable.addUser(userEnty1);
//        userTable.addUser(userEnty2);

        //开启Snmp监听，可以接收来自Trap端的信息。
        snmp.listen();

        /* UsmUserTable userTable = snmp.getUSM().getUserTable();
        userTable.addUser(userEnty1);
        userTable.addUser(userEnty2);*/

        /********************************************************************************************/
    }

    public void run(){
        try{
            init();
            snmp.addCommandResponder(this);
            System.out.println("开始监听trap消息!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 实现CommandResponder的processPdu方法, 用于处理传入的请求、PDU等信息
     * 当接收到trap时，会自动进入这个方法
     *
     * @param respEvnt
     */
    @Override
    public void processPdu(CommandResponderEvent event) {
        //解析response

    }
}
