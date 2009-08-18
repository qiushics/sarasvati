/*
    This file is part of Sarasvati.

    Sarasvati is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    Sarasvati is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with Sarasvati.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2008 Paul Lorenz
*/
package com.googlecode.sarasvati.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.googlecode.sarasvati.Engine;

public class DefaultExecutionEventQueue implements ExecutionEventQueue
{
  private List<RegisteredExecutionListener> listeners;

  public static ExecutionEventQueue newArrayListInstance ()
  {
    return new DefaultExecutionEventQueue( new ArrayList<RegisteredExecutionListener>() );
  }

  public static ExecutionEventQueue newCopyOnWriteListInstance ()
  {
    return new DefaultExecutionEventQueue( new CopyOnWriteArrayList<RegisteredExecutionListener>() );
  }

  DefaultExecutionEventQueue(final List<RegisteredExecutionListener> listeners)
  {
    this.listeners = listeners;
  }

  /**
   * @see com.googlecode.sarasvati.event.ExecutionEventQueue#addListener(com.googlecode.sarasvati.Engine, com.googlecode.sarasvati.event.ExecutionListener, com.googlecode.sarasvati.event.ExecutionEventType[])
   */
  public synchronized void addListener (final Engine engine,
                                        final ExecutionListener listener,
                                        final ExecutionEventType...eventTypes)
  {
    if ( eventTypes == null || listener == null)
    {
      return;
    }

    for ( ExecutionEventType eventType : eventTypes )
    {
      if (eventType != null)
      {
        listeners.add( new RegisteredExecutionListener( eventType, listener ) );
      }
    }
  }

  @Override
  public synchronized void removeListener (final Engine engine,
                                           final ExecutionListener listener,
                                           final ExecutionEventType... eventTypes)
  {
    if ( listener == null )
    {
      return;
    }

    List<ExecutionEventType> types = eventTypes == null ? null :  Arrays.asList( eventTypes );

    List<RegisteredExecutionListener> toRemove = new ArrayList<RegisteredExecutionListener>();

    for ( RegisteredExecutionListener wrapper : listeners )
    {
      if ( listener.getClass() == wrapper.listener.getClass() &&
           ( eventTypes == null || eventTypes.length == 0 || types.contains( wrapper.getEventType() ) ) )
      {
        toRemove.add( wrapper );
      }
    }

    listeners.removeAll( toRemove );
  }

  /**
   * @see com.googlecode.sarasvati.event.ExecutionEventQueue#fireEvent(com.googlecode.sarasvati.event.ExecutionEvent)
   */
  public EventActions fireEvent (final ExecutionEvent event)
  {
    EventActions eventActions = new EventActions();
    for (RegisteredExecutionListener wrapper : listeners )
    {
      if ( event.getEventType() == wrapper.getEventType() )
      {
        eventActions.compose( wrapper.notify( event ) );
      }
    }
    return eventActions;
  }

  static class RegisteredExecutionListener implements ExecutionListener
  {
    protected ExecutionEventType eventType;
    protected ExecutionListener listener;

    public RegisteredExecutionListener (final ExecutionEventType eventType,
                                        final ExecutionListener listener)
    {
      this.eventType = eventType;
      this.listener = listener;
    }

    public ExecutionEventType getEventType ()
    {
      return eventType;
    }

    public ExecutionListener getListener ()
    {
      return listener;
    }

    @Override
    public EventActions notify (final ExecutionEvent event)
    {
      return listener.notify( event );
    }
  }
}