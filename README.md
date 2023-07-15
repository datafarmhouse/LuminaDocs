# LuminaDocs 1.5.2

https://hub.docker.com/r/vanleemputteng/luminadocs/tags

Headless Document Generator

The Headless Document Generator is a versatile tool that allows you to effortlessly generate dynamic PDF documents based
on customizable templates. By providing a template containing variables and a corresponding JSON file with variable
values, this API-driven solution streamlines the process of creating professional-looking PDFs with ease.

Key Features:

1. Template-Based Generation: Define your document structure using a template that supports variables. Specify where
   variable values should be inserted, enabling dynamic content generation.
2. REST API: Supply a JSON containing the variable values that should be used in the template. This allows for flexible
   customization and data-driven document generation.
3. UI: a UI is available to manage your templates and css. This means you only have to send the template code and the
   variables to the API.

Whether you need to generate invoices, contracts, reports, or any other type of dynamic document, the Headless Document
Generator simplifies the process, empowering you to focus on the content while automating the PDF generation. Experience
the convenience and efficiency of this powerful tool for your document generation needs.

Sample request:
http POST on /api

```
{
    "template":{
        "debug": false,
        "engine":"freemarker",
        "css":{
            "code":"stored-css-code",
            "content":"actual css",
        },
        "code":"stored-template-code",
        "content":"<div class='text-orange-500 text-xl rounded-lg border p-2'>hello there: ${test} <br/> <span class='font-bold'>${nested.x}</span> <br/> ${nested.y} <br/> ${nested.y?string.currency}</div>",
        "variables":{
            "test":"general kenobi",
            "nested": {
                "x": "this is a nested value",
                "y": 24.5
            }
        }
    },
    "pdf": {
        "engine":"pdfbox"
    },
    "options":{
        "filename":"test.pdf"
    }
}
```

Minimal request:

```
{
    "template":{
        "code":"invoice"
    }
}
```

Template engine options:

- freemarker (default)
- handlebars
- mustache
- pebble

PDF engine options:

- pdfbox
- wkhtml (-e WKHTML_URL=http://wkhtml requires https://hub.docker.com/r/openlabs/docker-wkhtmltopdf-aas)

-e IMAGES_ADDRESS_URL=http://luminadocs:8080 is used for resolving images. In your template just
provide <img src="codeOfImageData"/>

I am open to requests.