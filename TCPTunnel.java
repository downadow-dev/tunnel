/*  This is free and unencumbered software released into the public domain.

    Anyone is free to copy, modify, publish, use, compile, sell, or
    distribute this software, either in source code form or as a compiled
    binary, for any purpose, commercial or non-commercial, and by any
    means.

    In jurisdictions that recognize copyright laws, the author or authors
    of this software dedicate any and all copyright interest in the
    software to the public domain. We make this dedication for the benefit
    of the public at large and to the detriment of our heirs and
    successors. We intend this dedication to be an overt act of
    relinquishment in perpetuity of all present and future rights to this
    software under copyright law.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.

    For more information, please refer to <http://unlicense.org/>

  or CC0 (see <https://freedomdefined.org/Licenses/CC-0>). */

package downadow.tcptunnel;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.net.*;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Input.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TCPTunnel implements ApplicationListener {
    ServerSocket server1, server2;
    Socket client1, client2;
    BufferedReader stream1, stream2;
    String addr;
    int port;
    
    public void create() {
        String[] data = Gdx.files.local(".tcptunnel").readString("UTF-8").replace("\n", "").split(":");
        addr = data[0];
        port = Integer.parseInt(data[1]);
        ServerSocketHints hints = new ServerSocketHints();
        hints.backlog = 1;
        hints.acceptTimeout = 0;
        
        server1 = Gdx.net.newServerSocket(Protocol.TCP, addr, port, hints);
        server2 = Gdx.net.newServerSocket(Protocol.TCP, addr, port + 1, hints);
        client1 = server1.accept(null);
        stream1 = new BufferedReader(new InputStreamReader(client1.getInputStream(), StandardCharsets.UTF_8));
        client2 = server2.accept(null);
        stream2 = new BufferedReader(new InputStreamReader(client2.getInputStream(), StandardCharsets.UTF_8));
        new Thread() {
            public void run() {
                try {
                    while(true) client2.getOutputStream().write((stream1.readLine() + "\n").getBytes(StandardCharsets.UTF_8));
                } catch(Exception ex) {}
            }
        }.start();
        
        try {
            while(true) client1.getOutputStream().write((stream2.readLine() + "\n").getBytes(StandardCharsets.UTF_8));
        } catch(Exception ex) {Gdx.app.log("info", "disconnect");}
    }
    
    public void resize(int width, int height) {}
    public void pause() {}
    public void resume() {}
    public void dispose() {}
    public void render() {}
}
