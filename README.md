# LuminaDocs
Headless Document Generator

The Headless Document Generator is a versatile tool that allows you to effortlessly generate dynamic PDF documents based on customizable templates. By providing a template containing variables and a corresponding JSON file with variable values, this API-driven solution streamlines the process of creating professional-looking PDFs with ease.

Key Features:

1. Template-Based Generation: Define your document structure using a template that supports variables. Specify where
   variable values should be inserted, enabling dynamic content generation.
2. REST API: Supply a JSON containing the variable values that should be used in the template. This allows for flexible
   customization and data-driven document generation.
3. Seamless Integration: Easily integrate the Headless Document Generator into your existing workflow or application
   using the provided API endpoints. Enjoy the convenience of generating PDFs programmatically without any manual
   intervention.
4. Extensive Customization: Tailor your document's appearance by designing templates with headers, footers, styling, and
   more. Create visually appealing PDFs that match your brand's identity and meet your specific requirements.
5. Efficient and Scalable: Built for performance, the Headless Document Generator optimizes resource utilization and can
   handle high volumes of document generation requests without compromising speed or reliability.
6. Documentation and Support: Benefit from comprehensive documentation that guides you through the setup and usage of
   the generator. Additionally, take advantage of a supportive community and dedicated support channels for assistance
   and troubleshooting.

Whether you need to generate invoices, contracts, reports, or any other type of dynamic document, the Headless Document
Generator simplifies the process, empowering you to focus on the content while automating the PDF generation. Experience
the convenience and efficiency of this powerful tool for your document generation needs.

Sample request:

```
{
    "engine": {
        "template":"freemarker",
        "pdf":"pdfbox"
    },
    "template":{
        "content":"<body>hello there: ${test} <br/> ${nested.x} <br/> ${nested.y} <br/> ${nested.y?string.currency}</body>",
        "variables":{
            "test":"general kenobi",
            "nested": {
                "x": "this is a nested value",
                "y": 24.5
            }
        }
    },
    "options":{
        "filename":"test.pdf"
    }
}
```

There are no other options for the engines just yet, but it has been setup so that others can be added easily.

I am open to requests.