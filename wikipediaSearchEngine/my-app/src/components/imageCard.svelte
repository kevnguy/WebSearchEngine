<script lang=ts>
    import { fade } from 'svelte/transition';    
	import { scale } from 'svelte/transition';
	import { quintOut } from 'svelte/easing';
    export let document: any;
</script>

<div in:scale={{duration: 150, easing: quintOut, opacity: 0}} out:fade={{duration: 150}}
    class="absolute top-1 right-2 border shadow-xl z-50 bg-white rounded-lg p-2 w-48">
    <img class="w-48" src={document.img} alt="Related">
</div>