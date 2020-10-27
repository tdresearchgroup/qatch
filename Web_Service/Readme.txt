In order to make the web service work you should include the following files into the 
current directory:

 1. onlineEvaluationScript.jar: This jar is responsible for the quality assessment of your GitHub Java project.
 2. build.xml, pmd_build.xml: These files are needed for the headless execution of the static analysis tools via ANT framework.
 3. config.xml: This file is needed in order to set the initial configuration of the overall aplication.

ATTENTION:

 - Ensure that the XML files containing the quality models show to the desired
   rulesets. In other words, you should open those XML files, find the properties
   that refer to PMD tool and check their "ruleset" attribute.At this attribute place 
   the exact path of the appropriate ruleset.xml file that should be used for the 
   property's quantification.

 - The downloadables (feature offered by the web service) are not included in this
   GitHub repository.