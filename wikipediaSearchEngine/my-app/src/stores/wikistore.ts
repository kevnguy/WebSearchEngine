import { writable } from "svelte/store";
export const documents = writable([]);

export const searchDocs = async (search: string, MultiField: boolean) => {
    let APIendpoint = 'http://localhost:8080/api/search?q=';
    let query = encodeURI(search);
    let type = (MultiField) ? "&model=MultiField" : "";

    console.log(APIendpoint+query+type);
    fetch(APIendpoint+query+type)
        .then(response => {
            if(!response.ok)
                throw new Error('Search API Responded with status of '+ response.status);
            // @ts-ignore: Object is possibly 'null'.
            document.getElementById("error").innerHTML = "";
            return response.json();
        })
        .then(data => {documents.set(data); return data;})
        .catch(error => {
            // @ts-ignore: Object is possibly 'null'.
            document.getElementById("error").innerHTML = "<span class=\"text-red-900 py-2\">"+
                        error.name+": "+error.message+"</span>";
            return query;
        });
}