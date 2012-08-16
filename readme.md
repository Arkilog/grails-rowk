# Grails Rowk Plugin
[![Build Status](https://secure.travis-ci.org/Arkilog/grails-rowk.png?branch=master)](http://travis-ci.org/Arkilog/grails-rowk)

## Introduction

This is a Grails plugin that allows you to create Workflows using a simple Groovy DSL.

## Concepts

Here a the concepts used in this plugin :

* _Workflow_ : a model of a sequence or network of states allowing different participants to collaborate on one or many business processes.
* _Process_ : an instance of a workflow activity at a given situation
* _State_ : a state as in finite state machines representing the current status of a workflow at a given situation.
* _Transition_ : a move from one Workflow State to another
* _Event_ : a user or automatic trigger of a Transition
* _Action_ : a processing task occuring during a Transition

## Example DSL

A code is worth a thousand words :

```Groovy
//Workflow definition
workflow(name :'onlineReporter'){
     //State definition
    start{
          //Transition definition (to state 'dispatch')
          updateArticle(to:'dispatch'){
             //Action definition
               run('articleService.update'){
                  //Action parameter definition (using variable)
                    id(ref:'params.id')
                    user(ref:'session.user')
                  //Action parameter definition (using constant)
                    override true
                  //Action result definition (whole result)
                    articleVersion
               }
               //No need to assign for a system state

               //Optional 'For your information' list (notification list)
               fyi{
                    role('authors')
               }
          }
          createArticle(to:'dispatch'){
               run('articleService.save'){
                    id(ref:'params.id')
                    user(ref:'session.user')
                    override true
                    articleVersion
               }
               //No need to assign for a system state

               //As mentionnted FYI can be used but not mandatory
               fyi{
                    role('authors')
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern) 
     //it's a system state no need to specify assignmments
     dispatch(type:'andfork'){
          requestControl(to :'control'){
               //'First in first out' assignment, the first participant that takes 
               // control of the following state will make the process progress
               //Mandatory here
               assign(type:'fifo'){
                    user(ref:'oldAuthor')
                    //group('johndoesworldwide')
                    //role('authors')
               }

          }
          requestReview(to:'review'){
               assign(type:'fifo'){
                    user('johndoe')
                    group('johndoesworldwide')
                    role('authors')
               }
               fyi{
                    user(ref:'oldAuthor')
                    group('johndoesworldwide')
                    role('authors')
               }
          }
     }
     //State definition
     control {
          askForRewrite(to :'start'){
               run('bossService.angry')
               assign(type:'vote',minpercent:100){
                    user('bigboss')
                    group('masterauthors')
                    role('experiencedauthors')
               }
          }
          approve(to :'publish'){
               run('bossService.happy')
               assign(type:'vote',minpercent:100){
                    user('bigboss')
                    group('masterauthors')
                    role('experiencedauthors')
               }
          }
          abort(to:'end')
     }
     //State definition
     review {
          ok(to :'publish'){
               run('articleService.preparePublishMailTemplate'){
                    user(ref:'author')
                    mail(ref:'authorEmail')
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    version(ref:'articleVersion')
                    subjectTemplate(to:'publishSubjectTemplate')
                    mailTemplate(to:'publishMailTemplate')
               }
               assign(type:'serial'){
                    user('teamleader')
                    user('manager')
                    user('bigboss')
               }
          }
          sendComments(to:'dispatch')
     }
     //State definition (AndJoin pattern)
     publish(type:'andjoin'){
          ok(to:'end'){
               run('mailService.sendPublishNotification'){
                    from(ref:'reviewerEmail')
                    destination(ref:'authorEmail')
                    subject(ref:'publishSubjectTemplate')
                    body(ref:'publishMailTemplate')
               }
               //No need to assign the end of the process
          }
          lastMinuteComments(to:'dispatch'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
               //No need to assign for a system state
          }
     }
     //State definition (an end state is mandatory)
     end
}
```
Please note that the DSL Language is still moving untill the 1.0.0 stable release.

## Roadmap

Please check the [1.0.0 milestone](grails-rowk/issues?milestone=1).
 
## License

Copyright 2012 Arkilog (www.arkilog.ma)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.