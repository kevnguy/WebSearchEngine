<script lang=ts>
	import { scale } from 'svelte/transition';
	import { quintOut } from 'svelte/easing';
    export let document: any;
    let hovering = false;
</script>

<div class="relative">
    {#if hovering}
        {#if document.img}
            <div in:scale={{duration: 150, easing: quintOut, opacity: 0}} 
                class="absolute top-0 right-0 border shadow-xl z-50 bg-white rounded-lg p-2 w-40">
                <img class="w-40" src={document.img} alt="Related">
            </div>
        {/if }
    {/if}
</div>