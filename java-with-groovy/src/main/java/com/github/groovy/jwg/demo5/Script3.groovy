println "In Script3"

bingding1 = new Binding()
bingding1.setProperty('name', 'Venkat')
shell = new GroovyShell(bingding1)
shell.evaluate(new File('Script1a.groovy'))

bingding2 = new Binding()
bingding2.setProperty('name', 'Dan')
shell.binding = bingding2
shell.evaluate(new File('Script1a.groovy'))