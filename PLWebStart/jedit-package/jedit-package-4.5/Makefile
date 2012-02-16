all:
	rm -f ../jedit-package-4.5.zip
	zip -r ../jedit-package-4.5.zip *
	cp ../jedit-package-4.5.zip ../../plwebstart/core

cdn:
	s3cmd -P put ../jedit-package-4.5.zip s3://cdn.plweb.org/plwebstart/core/

