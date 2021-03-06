[![Stories in Ready](https://badge.waffle.io/arkilog/grails-rowk.png?label=ready&title=Ready)](https://waffle.io/arkilog/grails-rowk)
# Grails Rowk Plugin
[![Build Status](https://secure.travis-ci.org/Arkilog/grails-rowk.png?branch=master)](http://travis-ci.org/Arkilog/grails-rowk)

## Introduction

### In one sentence

This is a Grails plugin that allows you to create Workflows using a simple Groovy DSL.

### Status

This is a work in progress, a the time of writing, only the domain model is assembled from the Groovy DSL.

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
          assignment{
               user(ref:'oldAuthor')
               group('johndoesworldwide')
               role('authors')
          }
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
               fyi{
                    role('elderManagement')
               }
          }
          createArticle(to:'dispatch'){
               run('articleService.save'){
                    id(ref:'params.id')
                    user(ref:'session.user')
                    override true
                    articleVersion
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern) 
     //it's a system state no need to specify assignmentmments
     dispatch(type:'andfork'){
          requestControl(to :'control')
          requestReview(to:'review'){
               fyi{
                    role('elderManagement')
               }
          }
     }
     //State definition
     control {
          assignment(type:'vote',minpercent:50){
               user('bigboss')
               group('masterauthors')
               role('experiencedauthors')
          }
          askForRewrite(to :'start'){
               run('bossService.angry')
          }
          approve(to :'syncadvices'){
               run('bossService.happy')
          }
          abort(to:'end')
     }
     //State definition
     review {
          assignment(type:'serial'){
               user('teamleader')
               user('manager')
          }
          ok(to :'syncadvices')
          sendComments(to:'start')
     }
     //State definition (AndJoin pattern)
     syncadvices(type:'andjoin'){
          go(to :'publish')
     }
     //State definition
     publish(type:'andjoin'){
          assignment{
               user(ref:'oldAuthor')
               group('johndoesworldwide')
               role('authors')
          }
          ok(to:'end'){
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
               run('mailService.sendPublishNotification'){
                    from(ref:'reviewerEmail')
                    destination(ref:'authorEmail')
                    subject(ref:'publishSubjectTemplate')
                    body(ref:'publishMailTemplate')
               }
          }
          lastMinuteComments(to:'dispatch'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
          }
     }
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