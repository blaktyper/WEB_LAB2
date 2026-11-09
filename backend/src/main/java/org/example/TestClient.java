
package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient {
    private static void sendRecord(DataOutputStream out, int type, byte[] data) throws IOException {
        out.writeByte(1);
        out.writeByte(type);
        out.writeShort(1);
        out.writeShort(data.length);
        out.writeByte(0);
        out.writeByte(0);
        out.write(data);
        out.flush();
    }

    private static byte[] encodeParams(String name, String value) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

        buffer.write(nameBytes.length);
        buffer.write(valueBytes.length);
        buffer.write(nameBytes);
        buffer.write(valueBytes);

        return buffer.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 24001)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            byte[] beginRequest = {
                    0, 1, 0, 0, 0, 0, 0, 0
            };

            sendRecord(out, 1, beginRequest);

            ByteArrayOutputStream params = new ByteArrayOutputStream();
            params.write(encodeParams("REQUEST_METHOD", "POST"));
            params.write(encodeParams("CONTENT_TYPE", "application/x-www-form-urlencoded"));
            params.write(encodeParams("CONTENT_LENGTH", "11"));

            sendRecord(out, 4, params.toByteArray());
            sendRecord(out, 4, new byte[0]);

            sendRecord(out, 5, "x=1&y=1&r=2".getBytes(StandardCharsets.UTF_8));
            sendRecord(out, 5, new byte[0]);

            while (true) {
                int version = in.readUnsignedByte();
                int type = in.readUnsignedByte();
                in.readUnsignedShort();

                int contentLength = in.readUnsignedShort();
                int paddingLength = in.readUnsignedByte();
                in.readUnsignedByte();

                byte[] content = in.readNBytes(contentLength);
                in.skipNBytes(paddingLength);

                if (type == 6) {
                    System.out.print(new String(content, StandardCharsets.UTF_8));
                }

                if (type == 7) {
                    System.err.print(new String(content, StandardCharsets.UTF_8));
                }

                if (type == 3) {
                    break;
                }
            }
        }
    }
}
