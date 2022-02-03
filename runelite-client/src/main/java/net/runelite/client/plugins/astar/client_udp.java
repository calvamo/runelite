package net.runelite.client.plugins.astar;

import java.io.IOException;
import java.net.*;

public class client_udp {

        private DatagramSocket socket;
        private InetAddress address;

        private byte[] buf;

        public client_udp() throws SocketException, UnknownHostException {
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
        }

        public String sendEcho(String msg,int port) throws IOException {
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            //packet = new DatagramPacket(buf, buf.length);
            //socket.receive(packet);
            //String received = new String(
            //        packet.getData(), 0, packet.getLength());
            //return received;
            return "hi";
        }
        public String sendandRec(String msg,int port) throws IOException {
            buf = msg.getBytes();
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
    }
        public void close() {
            socket.close();
        }

}
