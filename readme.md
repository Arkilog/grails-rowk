# Grails Rowk Plugin
[![Build Status](https://secure.travis-ci.org/yellowsnow/grails-rowk.png?branch=master)](http://travis-ci.org/yellowsnow/grails-rowk)

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
workflow(name :'onlineReporter'){
     //State definition
    start{
          //Transition definition (to state 'edit')
        selectArticle(to:'edit'){
             //Action definition
               run('rowkService.userInfo'){
                  //Action parameter definition (using variable)
                    user(to:'author')
                  //Action result definition
                    userEmail(to:'authorEmail')
               }
               run('articleService.update'){
                    id(ref:'articleId')
                    user(ref:'author')
                  //Action parameter definition (using constant)
                    override true
                  //Action result definition (whole result)
                    articleVersion
               }
          }
          createArticle(to:'edit'){
               run('rowkService.userInfo'){
                    user(to:'author')
                    userEmail(to:'authorEmail')
               }
               run('articleService.save'){
                    id(ref:'articleId')
                    user(ref:'author')
                    override true
                    articleVersion
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern)
     edit(type:'andfork'){
          requestControl(to :'control')
          requestReview(to:'review')
     }
     //State definition
     control {
          askForRewrite(to :'edit'){
               run('bossService.angry')
          }
          approve(to :'publish'){
               run('bossService.happy')
          }
          abort(to:'end')
     }
     //State definition
     review {
          ok(to :'publish'){
               run('rowkService.userInfo'){
                    user(to:'reviewerName')
                    userEmail(to:'reviewerEmail')
               }
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
          }
          sendComments(to:'edit')
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
          }
          lastMinuteComments(to:'edit'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
          }
     }
     //State definition (an end state is mandatory)
     end
}
```
Please note that the DSL Language is still moving untill the 1.0.0 stable release.

## Roadmap

Please check the [1.0.0 milestone](yellowsnow/grails-rowk/issues?milestone=1).
 
