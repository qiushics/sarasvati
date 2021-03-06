package com.googlecode.sarasvati.example;

import java.util.Calendar;

import com.googlecode.sarasvati.Engine;
import com.googlecode.sarasvati.GuardResponse;
import com.googlecode.sarasvati.NodeToken;
import com.googlecode.sarasvati.rubric.RubricInterpreter;
import com.googlecode.sarasvati.rubric.env.DefaultRubricEnv;
import com.googlecode.sarasvati.rubric.env.DefaultRubricFunctionRepository;
import com.googlecode.sarasvati.rubric.env.RubricEnv;
import com.googlecode.sarasvati.rubric.env.RubricPredicate;

public class IsWeekendPredicate implements RubricPredicate
{
  @Override
  public boolean eval (Engine engine, NodeToken token)
  {
    Calendar cal = Calendar.getInstance();
    int day = cal.get( Calendar.DAY_OF_WEEK );
    return day == Calendar.SATURDAY ||
           day == Calendar.SUNDAY;
  }

  public static void main (String[] args) throws Exception
  {
    DefaultRubricFunctionRepository repository = DefaultRubricFunctionRepository.getGlobalInstance();
    repository.registerPredicate( "isWeekend", new IsWeekendPredicate() );

    String stmt = "if isWeekend then Accept else Skip";

    RubricEnv env = new DefaultRubricEnv( null, null, repository );
    GuardResponse response = (GuardResponse)RubricInterpreter.compile( stmt ).eval( env );

    System.out.println( response );
  }
}
