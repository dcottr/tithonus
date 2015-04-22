package server;


import game.GameEngine;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.luaj.vm2.LuaError;

import ai.LuaAIError;


public class ServerManager extends AbstractHandler {

	
	public static void main(String[] args) throws Exception {
		
		
        Server server = new Server(4000);
        
        ContextHandler contextAI = new ContextHandler("/match/ai");
        contextAI.setHandler(new ServerManager());
        
       // server.setHandler(contextAI);
        
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });

        resource_handler.setResourceBase("./tithonus-client/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, contextAI });
        server.setHandler(handlers);

 
        server.start();
        server.join();
	}

	@Override
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		ArrayList<String> pNames = Collections.list(request.getParameterNames());
		Collections.sort(pNames);
		LinkedList<String> aiStrings = new LinkedList<>();
		for (String param : pNames) {
			String ai = request.getParameter(param);
		       aiStrings.add(ai);
		}
		
    	if (aiStrings.isEmpty()) return;
    	
    	System.out.println(request.getContextPath());
    	System.out.println(request.getPathInfo());
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        String[] playerAIs = (String[]) aiStrings.toArray(new String[aiStrings.size()]);
        String jsonString = runGame(playerAIs);
                
        response.getWriter().println(jsonString);

    	
	}
	
	private String runGame(String[] playerAIs) throws FileNotFoundException {
        OutputStream jsonStream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(jsonStream);
		try {
			GameEngine engine = new GameEngine(playerAIs);
			GameEncoder encoder = new GameEncoder(engine.gameState);
					
			engine.addObserver(encoder);
			engine.start();
			
	        jsonWriter.writeObject(encoder.getGameJson());

		} catch (LuaAIError e) {
			 JsonObject error = Json.createObjectBuilder()
				     .add("luaError", e.getMessage())
				     .add("playerID", e.getPlayerID()).build();
			 jsonWriter.writeObject(error);
		}
        jsonWriter.close();
		String jsonString = jsonStream.toString();
		return jsonString;
	}

}
