package com.cowboysmall.noblox.handler;

import com.cowboysmall.noblox.Channel;
import com.cowboysmall.noblox.Handle;
import com.cowboysmall.noblox.Reactor;
import com.cowboysmall.noblox.RequestContext;
import com.cowboysmall.noblox.ServerContext;


public class AcceptHandler implements Handler {

    private ServerContext serverContext;


    //_________________________________________________________________________

    public AcceptHandler(ServerContext serverContext) {

        this.serverContext = serverContext;
    }


    //_________________________________________________________________________

    @Override
    public void handle() {

        try {

            Channel channel = serverContext.getAcceptor().accept();
            Reactor reactor = serverContext.getNextReactor();

            reactor.lock();
            try {

                reactor.wakeup();

                Handle handle = reactor.registerReadInterest(channel);
                handle.setAttachment(
                        new ReadHandler(
                                handle,
                                serverContext,
                                new RequestContext(
                                        serverContext.getBuffer(),
                                        serverContext.getBuffer()
                                )
                        )
                );

            } finally {

                reactor.unlock();
            }

        } catch (Exception e) {

            throw new HandlerException(e);
        }
    }
}